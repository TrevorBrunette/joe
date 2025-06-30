package pro.trevor.joe.program.llvm;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import pro.trevor.joe.parser.tree.declaration.Access;
import pro.trevor.joe.program.File;
import pro.trevor.joe.program.Parameter;
import pro.trevor.joe.program.analyzer.TypeAnalyzer;
import pro.trevor.joe.program.code.Expressions;
import pro.trevor.joe.program.code.Function;
import pro.trevor.joe.program.code.Statements;
import pro.trevor.joe.program.extern.ExternFunction;
import pro.trevor.joe.program.extern.ExternVariant;
import pro.trevor.joe.program.type.*;
import pro.trevor.joe.util.Pair;

import java.util.List;
import java.util.Optional;

import static org.bytedeco.llvm.global.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildStore;

public class Generator implements AutoCloseable {

    private static final String MALLOC_NAME = "malloc";
    private static final TypeReference MALLOC_ARG = new PrimitiveTypeReference(Primitive.U64);
    private static final TypeReference MALLOC_RET = new ArrayTypeReference(new PrimitiveTypeReference(Primitive.U8));

    private final File file;
    private final java.io.File output;

    final TypeAnalyzer typeAnalyzer;
    Function currentFunction;

    private int statements;

    final LlvmInstance llvm;

    public Generator(File file, java.io.File output, String triple) {
        this.file = file;
        this.output = output;
        this.typeAnalyzer = new TypeAnalyzer(this.file);
        this.currentFunction = null;
        this.statements = 0;
        this.llvm = new LlvmInstance(file.getName().split("\\.")[0], triple);
    }

    public Generator(File file, java.io.File output) {
        this.file = file;
        this.output = output;
        this.typeAnalyzer = new TypeAnalyzer(this.file);
        this.currentFunction = null;
        this.statements = 0;
        this.llvm = new LlvmInstance(file.getName().split("\\.")[0]);
    }


    public boolean generate() {
        addUndeclaredExternFunctions();
        addExternalFunctions();
        addDeclaredFunction(file.getFunctions().stream().filter((f) -> f.getIdentifier().equals("main")).findFirst().get());
        if (!write())  {
            return false;
        }
        return validate();
    }

    private void addUndeclaredExternFunctions() {
        addExternalFunction(new ExternFunction(Access.PRIVATE, ExternVariant.C, MALLOC_NAME, List.of(MALLOC_ARG), MALLOC_RET, false));
    }

    private void addExternalFunctions() {
        file.getExternFunctions().forEach(this::addExternalFunction);
    }

    private void addDeclaredFunction(Function function) {
        this.currentFunction = function;
        LLVMValueRef llvmFunction = LLVMGetNamedFunction(llvm.module, function.getIdentifier());
        if (llvmFunction == null) {
            LLVMTypeRef returnType = getLLVMType(function.getReturnType());
            LLVMTypeRef[] paramTypes = function.getParameters().stream().map(Parameter::type).map(this::getLLVMType).toArray(LLVMTypeRef[]::new);
            PointerPointer<LLVMTypeRef> params = new PointerPointer<>(paramTypes);
            llvmFunction = LLVMAddFunction(llvm.module, function.getIdentifier(), LLVMFunctionType(returnType, params, paramTypes.length, 0));
        }

        resetStatementCount();
        this.typeAnalyzer.getContext().resetLocals();
        LLVMBasicBlockRef entry = LLVMAppendBasicBlockInContext(llvm.ctx, llvmFunction, "entry");
        for (Statements.Statement statement : function.getCodeBlock().statements()) {
            addStatement(entry, statement);
        }
        this.currentFunction = null;
    }

    private void addExternalFunction(ExternFunction function) {
        LLVMTypeRef returnType = getLLVMType(function.returnType());
        LLVMTypeRef[] paramTypes = function.parameters().stream().map(this::getLLVMType).toArray(LLVMTypeRef[]::new);
        PointerPointer<LLVMTypeRef> params = new PointerPointer<>(paramTypes);
        LLVMAddFunction(llvm.module, function.name(), LLVMFunctionType(returnType, params, paramTypes.length, function.varArg() ? 1 : 0));
    }

    void addStatement(LLVMBasicBlockRef block, Statements.Statement statement) {
        LLVMPositionBuilderAtEnd(llvm.builder, block);
        switch (statement) {
            case Statements.Expression expressionStatement -> {
                addExpression(block, expressionStatement.expression());
            }
            case Statements.Return returnStatement -> {
                StatementGenerator.returnStatement(this, block, returnStatement);
            }
            case Statements.VariableDeclaration variableDeclarationStatement -> {
                StatementGenerator.variableDeclaration(this, variableDeclarationStatement);
            }
            case Statements.VariableInitialization variableInitializationStatement -> {
                StatementGenerator.variableInitialization(this, block, variableInitializationStatement);
            }
            case Statements.Empty empty -> {
                // Intentionally left blank
            }
            default -> throw new IllegalStateException("Unimplemented statement type: " + statement.getClass().getSimpleName());
        }
    }

    LLVMValueRef addExpression(LLVMBasicBlockRef block, Expressions.Expression expression) {
        LLVMPositionBuilderAtEnd(llvm.builder, block);
        typeAnalyzer.analyze(expression);

        switch (expression) {
            case Expressions.MethodInvocation methodInvocation -> {
                String name = ((Expressions.Variable) methodInvocation.method()).name();
                LLVMValueRef fn = LLVMGetNamedFunction(llvm.module, name);
                LLVMTypeRef fnType = LLVMGetGEPSourceElementType(fn);

                int fnParams = LLVMCountParamTypes(fnType);
                int providedParams = methodInvocation.arguments().size();
                PointerPointer<LLVMTypeRef> paramTypesPtr = new PointerPointer<>(new LLVMTypeRef[fnParams]);
                LLVMGetParamTypes(fnType, paramTypesPtr);
                LLVMValueRef[] arguments = new LLVMValueRef[providedParams];


                if (LLVMIsFunctionVarArg(fnType) != 0) {
                    if (fnParams <= providedParams) {
                        for (int i = 0; i < fnParams; ++i) {
                            LLVMTypeRef paramType = paramTypesPtr.get(LLVMTypeRef.class, i);
                            arguments[i] = Util.cast(this, addExpression(block, methodInvocation.arguments().get(i)), paramType);
                        }
                        for (int i = fnParams; i < providedParams; ++i) {
                            arguments[i] = addExpression(block, methodInvocation.arguments().get(i));
                        }
                    } else {
                        throw new IllegalStateException("Incorrect number of arguments for var-arg function " + methodInvocation.method() + "; expected at least " + fnParams + ", found " + providedParams);
                    }
                } else {
                    if (fnParams == providedParams) {
                        for (int i = 0; i < fnParams; ++i) {
                            LLVMTypeRef paramType = paramTypesPtr.get(LLVMTypeRef.class, i);
                            arguments[i] = Util.cast(this, addExpression(block, methodInvocation.arguments().get(i)), paramType);
                        }
                    } else {
                        throw new IllegalStateException("Incorrect number of arguments for function " + methodInvocation.method() + "; expected " + fnParams + ", found " + providedParams);
                    }
                }

                PointerPointer<LLVMValueRef> args = new PointerPointer<>(arguments);
                return LLVMBuildCall2(llvm.builder, fnType, fn, args, arguments.length, name + ".call." + getStatementCount());
            }
            case Expressions.String stringExpression -> {
                return LLVMBuildGlobalStringPtr(llvm.builder, stringExpression.value(), stringExpression.value());
            }
            case Expressions.Integer integerExpression -> {
                LLVMTypeRef i128type = LLVMInt128TypeInContext(llvm.ctx);
                return LLVMConstInt(i128type, integerExpression.value().longValueExact(), 1);
            }
            case Expressions.Float floatExpression -> {
                LLVMTypeRef f128type = LLVMFP128TypeInContext(llvm.ctx);
                return LLVMConstReal(f128type, floatExpression.value().doubleValue());
            }
            case Expressions.Char charExpression -> {
                LLVMTypeRef i8Type = LLVMInt8TypeInContext(llvm.ctx);
                return LLVMConstInt(i8Type, charExpression.value(), 0);
            }
            case Expressions.Boolean boolExpression -> {
                LLVMTypeRef i1Type = LLVMInt1TypeInContext(llvm.ctx);
                return LLVMConstInt(i1Type, boolExpression.value() ? 1 : 0, 0);
            }
            case Expressions.Addition additionExpression -> {
                return LLVMBuildAdd(llvm.builder, addExpression(block, additionExpression.left()), addExpression(block, additionExpression.right()), "add." + getStatementCount());
            }
            case Expressions.Subtraction subtractionExpression -> {
                return LLVMBuildSub(llvm.builder, addExpression(block, subtractionExpression.left()), addExpression(block, subtractionExpression.right()), "sub." + getStatementCount());
            }
            case Expressions.Multiply multiplyExpression -> {
                return LLVMBuildMul(llvm.builder, addExpression(block, multiplyExpression.left()), addExpression(block, multiplyExpression.right()), "mul." + getStatementCount());
            }
            case Expressions.Divide divideExpression -> {
                return LLVMBuildSDiv(llvm.builder, addExpression(block, divideExpression.left()), addExpression(block, divideExpression.right()), "div." + getStatementCount());
            }
            case Expressions.Modulo moduloExpression -> {
                return LLVMBuildSRem(llvm.builder, addExpression(block, moduloExpression.left()), addExpression(block, moduloExpression.right()), "mod." + getStatementCount());
            }
            case Expressions.Variable variableExpression -> {
                LLVMValueRef value;
                String name = variableExpression.name();
                Optional<LLVMValueRef> maybeValue = this.typeAnalyzer.getContext().getLocalStorage(name);
                if (maybeValue.isPresent()) {
                    value = maybeValue.get();
                } else {
                    throw new IllegalStateException("Undeclared variable: " + name);
                }
                return LLVMBuildLoad2(llvm.builder, LLVMGetAllocatedType(value), value, name);
            }
            case Expressions.Assignment assignmentExpression -> {
                LLVMValueRef rhsValue = addExpression(block, assignmentExpression.right());
                if (assignmentExpression.left() instanceof Expressions.Variable(String name)) {
                    Optional<LLVMValueRef> maybeValue = this.typeAnalyzer.getContext().getLocalStorage(name);
                    if (maybeValue.isPresent()) {
                        LLVMValueRef storagePtr = maybeValue.get();
                        LLVMTypeRef storageType = LLVMGetAllocatedType(storagePtr);
                        LLVMValueRef castValue = Util.cast(this, rhsValue, storageType);
                        return LLVMBuildStore(llvm.builder, castValue, storagePtr);
                    } else {
                        throw new IllegalStateException("Undeclared variable of name: " + name);
                    }
                } else if (assignmentExpression.left() instanceof Expressions.ArrayIndex arrayIndexExpression) {
                    Pair<TypeReference, LLVMValueRef> typeAndPtr = arrayIndex(block, arrayIndexExpression.array(), arrayIndexExpression.index());
                    LLVMTypeRef elementType = getLLVMType(typeAndPtr.getLeft());
                    LLVMValueRef valuePtr = typeAndPtr.getRight();
                    LLVMValueRef toStore = Util.cast(this, rhsValue, elementType);
                    return LLVMBuildStore(llvm.builder, toStore, valuePtr);
                } else {
                    throw new IllegalStateException("Unimplemented assignment for LHS: " + assignmentExpression.left().getClass().getSimpleName());
                }
            }
            case Expressions.ArrayIndex arrayIndexExpression -> {
                Pair<TypeReference, LLVMValueRef> typeAndPtr = arrayIndex(block, arrayIndexExpression.array(), arrayIndexExpression.index());
                LLVMTypeRef elementType = getLLVMType(typeAndPtr.getLeft());
                LLVMValueRef valuePtr = typeAndPtr.getRight();
                return LLVMBuildLoad2(llvm.builder, elementType, valuePtr, "array.access." + getStatementCount());
            }
            case Expressions.ArrayInstantiation arrayInstantiationExpression -> {
                LLVMValueRef mallocFn = LLVMGetNamedFunction(llvm.module, "malloc");
                LLVMTypeRef mallocFnType = LLVMGetGEPSourceElementType(mallocFn);
                LLVMTypeRef mallocFnArgType = getLLVMType(MALLOC_ARG);

                LLVMTypeRef arrayElementType = getLLVMType(arrayInstantiationExpression.type());
                LLVMValueRef numElements = addExpression(block, arrayInstantiationExpression.size());
                LLVMValueRef numElementsCast = Util.cast(this, numElements, mallocFnArgType);
                LLVMValueRef arrayElementTypeSize = LLVMConstInt(mallocFnArgType, LLVMABISizeOfType(llvm.targetData, arrayElementType), 0);
                LLVMValueRef arraySize = LLVMBuildMul(llvm.builder, arrayElementTypeSize, numElementsCast, "array.size." + getStatementCount());

                LLVMValueRef mallocSize = Util.cast(this, arraySize, mallocFnArgType);
                LLVMValueRef mallocResult = LLVMBuildCall2(llvm.builder, mallocFnType, mallocFn, new PointerPointer<>(new LLVMValueRef[]{mallocSize}), 1, "array.malloc." + getStatementCount());

                return LLVMBuildBitCast(llvm.builder, mallocResult, LLVMPointerType(arrayElementType, 0), "array.cast." + getStatementCount());
            }
            default -> throw new IllegalStateException("Unimplemented expression type: " + expression.getClass().getSimpleName());
        }
    }

    private boolean validate() {
        BytePointer error = new BytePointer();
        if (LLVMVerifyModule(llvm.module, LLVMPrintMessageAction, error) != 0) {
            System.err.println("Failed to validate module: " + error.getString());
            LLVMDisposeMessage(error);
            return false;
        }
        return true;
    }

    private boolean write() {
        BytePointer error = new BytePointer();
        System.out.println(LLVMPrintModuleToString(llvm.module).getString());
        if (LLVMPrintModuleToFile(llvm.module, this.output.toString(), error) != 0) {
            System.err.println("Failed to write module: " + error.getString());
            LLVMDisposeMessage(error);
            return false;
        }
        return true;
    }

    private Pair<TypeReference, LLVMValueRef> arrayIndex(LLVMBasicBlockRef block, Expressions.YieldingExpression array, Expressions.YieldingExpression index) {
        LLVMValueRef arrayPointer = addExpression(block, array);
        LLVMValueRef indexValue = addExpression(block, index);
        LLVMTypeRef ptrSize = getLLVMType(llvm.ptrSize);
        LLVMValueRef arrayPtrCast = Util.ptrToInt(this, arrayPointer, ptrSize);
        LLVMValueRef indexCast = Util.cast(this, indexValue, ptrSize);
        ArrayTypeReference arrayPtrTypeRef = (ArrayTypeReference) typeAnalyzer.getType(array).get();
        LLVMTypeRef arrayElementType = getLLVMType(arrayPtrTypeRef.contained());
        LLVMValueRef arrayElementTypeSize = LLVMConstInt(ptrSize, LLVMABISizeOfType(llvm.targetData, arrayElementType), 0);
        LLVMValueRef indexOffset = LLVMBuildMul(llvm.builder, arrayElementTypeSize, indexCast, "array.index." + getStatementCount());
        LLVMValueRef arrayTargetValueSrc = LLVMBuildAdd(llvm.builder, arrayPtrCast, indexOffset, "array.target." + getStatementCount());
        return new Pair<>(arrayPtrTypeRef.contained(), Util.intToPtr(this, arrayTargetValueSrc, LLVMTypeOf(arrayPointer)));
    }

    @Override
    public void close() {
        llvm.close();
    }

    void resetStatementCount() {
        this.statements = 0;
    }

    int getStatementCount() {
        return this.statements++;
    }

    LLVMTypeRef getLLVMType(TypeReference type) {
        switch (type) {
            case PrimitiveTypeReference(Primitive primitive) -> {
                switch (primitive) {
                    case I8, U8 -> {
                        return LLVMInt8TypeInContext(llvm.ctx);
                    }
                    case I16, U16 -> {
                        return LLVMInt16TypeInContext(llvm.ctx);
                    }
                    case I32, U32 -> {
                        return LLVMInt32TypeInContext(llvm.ctx);
                    }
                    case I64, U64 -> {
                        return LLVMInt64TypeInContext(llvm.ctx);
                    }
                    case I128, U128 -> {
                        return LLVMInt128TypeInContext(llvm.ctx);
                    }
                    case BOOL -> {
                        return LLVMInt1TypeInContext(llvm.ctx);
                    }
                    case F32 -> {
                        return LLVMFloatTypeInContext(llvm.ctx);
                    }
                    case F64 -> {
                        return LLVMDoubleTypeInContext(llvm.ctx);
                    }
                    case VOID -> {
                        return LLVMVoidTypeInContext(llvm.ctx);
                    }
                }
            }
            case ArrayTypeReference(TypeReference contained) -> {
                return LLVMPointerType(getLLVMType(contained), 0);
            }
            case NamedTypeReference namedTypeReference ->{
                throw new Error("Unhandled named type reference: " + namedTypeReference.name());
            }
            case null, default -> {
                throw new Error("Unhandled type: " + type);
            }
        }
        throw new Error("Internal error for unhandled type: " + type);
    }
}
