package pro.trevor.joe.program.llvm;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import pro.trevor.joe.program.File;
import pro.trevor.joe.program.Parameter;
import pro.trevor.joe.program.code.Expressions;
import pro.trevor.joe.program.code.Function;
import pro.trevor.joe.program.code.Statements;
import pro.trevor.joe.program.extern.ExternFunction;
import pro.trevor.joe.program.type.*;

import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.javacpp.LLVM.*;

public class Generator implements AutoCloseable {

    private final File file;
    private final java.io.File output;

    private final LLVMContextRef ctx;
    private final LLVMModuleRef module;
    private final LLVMBuilderRef builder;

    private final BytePointer error = new BytePointer();

    private Map<String, LLVMValueRef> locals;
    private Function currentFunction;

    private int statements;

    public Generator(File file, java.io.File output) {
        this.file = file;
        this.output = output;
        this.ctx = LLVMContextCreate();
        this.module = LLVMModuleCreateWithNameInContext(file.getName().split("\\.")[0], this.ctx);
        this.builder = LLVMCreateBuilderInContext(this.ctx);
        this.currentFunction = null;
        this.statements = 0;
    }

    public boolean generate() {
        addExternalFunctions();
        addDeclaredFunction(file.getFunctions().stream().filter((f) -> f.getIdentifier().equals("main")).findFirst().get());
        write();
        return validate();
    }

    private void addExternalFunctions() {
        file.getExternFunctions().forEach(this::addExternalFunction);
    }

    private void addDeclaredFunction(Function function) {
        this.currentFunction = function;
        LLVMValueRef llvmFunction = LLVMGetNamedFunction(this.module, function.getIdentifier());
        if (llvmFunction == null) {
            LLVMTypeRef returnType = getLLVMType(function.getReturnType());
            LLVMTypeRef[] paramTypes = function.getParameters().stream().map(Parameter::type).map(this::getLLVMType).toArray(LLVMTypeRef[]::new);
            PointerPointer<LLVMTypeRef> params = new PointerPointer<>(paramTypes);
            llvmFunction = LLVMAddFunction(this.module, function.getIdentifier(), LLVMFunctionType(returnType, params, paramTypes.length, 0));
        }

        resetStatementCount();
        locals = new HashMap<>();
        LLVMBasicBlockRef entry = LLVMAppendBasicBlockInContext(this.ctx, llvmFunction, "entry");
        for (Statements.Statement statement : function.getCodeBlock().statements()) {
            addStatement(entry, statement);
        }
        this.currentFunction = null;
    }

    private void addExternalFunction(ExternFunction function) {
        LLVMTypeRef returnType = getLLVMType(function.returnType());
        LLVMTypeRef[] paramTypes = function.parameters().stream().map(this::getLLVMType).toArray(LLVMTypeRef[]::new);
        PointerPointer<LLVMTypeRef> params = new PointerPointer<>(paramTypes);
        LLVMAddFunction(this.module, function.name(), LLVMFunctionType(returnType, params, paramTypes.length, function.varArg() ? 1 : 0)).close();
    }

    private void addStatement(LLVMBasicBlockRef block, Statements.Statement statement) {
        LLVMPositionBuilderAtEnd(this.builder, block);
        switch (statement) {
            case Statements.Expression expressionStatement -> {
                addExpression(block, expressionStatement.expression());
            }
            case Statements.Return returnStatement -> {
                if (returnStatement.value() == null) {
                    if (currentFunction.getReturnType() instanceof PrimitiveTypeReference(Primitive primitive)) {
                        if (primitive != Primitive.VOID) {
                            throw new IllegalStateException("Returning a value when expected return type is void");
                        }
                    }
                    LLVMBuildRetVoid(this.builder);
                } else {
                    LLVMValueRef returnValue = addExpression(block, returnStatement.value());
                    if (currentFunction.getReturnType() instanceof PrimitiveTypeReference primitiveTypeReference) {
                        Primitive primitive = primitiveTypeReference.primitive();
                        if (primitive == Primitive.VOID) {
                            throw new IllegalStateException("Returning void when expected return type is: " + primitive.name());
                        } else {
                            // Cast the expression result to the desired return type
                            LLVMTypeRef type = getLLVMType(primitiveTypeReference);
                            LLVMTypeRef rhsType = LLVMTypeOf(returnValue);

                            if (!rhsType.equals(type)) {
                                switch (LLVMGetTypeKind(type)) {
                                    case LLVMIntegerTypeKind -> {
                                        returnValue = LLVMBuildIntCast(this.builder, returnValue, type, getStatementCount() + ".cast");
                                    }
                                    case LLVMFloatTypeKind, LLVMDoubleTypeKind, LLVMHalfTypeKind -> {
                                        returnValue = LLVMBuildFPCast(this.builder, returnValue, type, getStatementCount() + ".cast");
                                    }
                                    default -> {
                                        throw new IllegalStateException("Unimplemented implicit cast for RHS type kind " +  LLVMGetTypeKind(type));
                                    }
                                }
                            }
                        }
                    }
                    LLVMBuildRet(this.builder, returnValue);
                }
            }
            case Statements.VariableDeclaration variableDeclarationStatement -> {
                switch (variableDeclarationStatement.type()) {
                    case PrimitiveTypeReference primitiveTypeReference -> {
                        LLVMTypeRef type = getLLVMType(primitiveTypeReference);
                        LLVMValueRef alloca = LLVMBuildAlloca(this.builder, type, variableDeclarationStatement.name());
                        locals.put(variableDeclarationStatement.name(), alloca);
                    }
                    default -> throw new IllegalStateException("Unhandled declaration type: " + variableDeclarationStatement.type().getClass().getSimpleName());
                }
            }
            case Statements.VariableInitialization variableInitializationStatement -> {
                switch (variableInitializationStatement.type()) {
                    case PrimitiveTypeReference primitiveTypeReference -> {
                        LLVMTypeRef type = getLLVMType(primitiveTypeReference);
                        LLVMValueRef alloca = LLVMBuildAlloca(this.builder, type, variableInitializationStatement.name());
                        locals.put(variableInitializationStatement.name(), alloca);

                        LLVMValueRef rhsValue = addExpression(block, variableInitializationStatement.value());
                        LLVMTypeRef rhsType = LLVMTypeOf(rhsValue);

                        LLVMValueRef cast = rhsValue;
                        if (!rhsType.equals(type)) {
                            switch (LLVMGetTypeKind(type)) {
                                case LLVMIntegerTypeKind -> {
                                    cast = LLVMBuildIntCast(this.builder, rhsValue, type, variableInitializationStatement.name() + "." + getStatementCount() + ".cast");
                                }
                                case LLVMFloatTypeKind, LLVMDoubleTypeKind, LLVMHalfTypeKind -> {
                                    cast = LLVMBuildFPCast(this.builder, rhsValue, type, variableInitializationStatement.name() + "." + getStatementCount() + ".cast");
                                }
                                default -> {
                                    throw new IllegalStateException("Unimplemented implicit cast for RHS type kind " +  LLVMGetTypeKind(type));
                                }
                            }
                        }
                        LLVMBuildStore(this.builder, cast, alloca);
                    }
                    default -> throw new IllegalStateException("Unhandled declaration type: " + variableInitializationStatement.type().getClass().getSimpleName());
                }
            }
            case Statements.Empty empty -> {
                // Intentionally left blank
            }
            default -> throw new IllegalStateException("Unimplemented statement type: " + statement.getClass().getSimpleName());
        }
    }

    private LLVMValueRef addExpression(LLVMBasicBlockRef block, Expressions.Expression expression) {
        LLVMPositionBuilderAtEnd(this.builder, block);
        switch (expression) {
            case Expressions.MethodInvocation methodInvocation -> {
                String name = ((Expressions.Variable) methodInvocation.method()).name();
                LLVMValueRef[] arguments = methodInvocation.arguments().stream()
                        .map((argument) -> addExpression(block, argument))
                        .toArray(LLVMValueRef[]::new);
                LLVMValueRef fn = LLVMGetNamedFunction(this.module, name);
                PointerPointer<LLVMValueRef> args = new PointerPointer<>(arguments);
                return LLVMBuildCall(this.builder, fn, args, arguments.length, getStatementCount() + "_call_" + name);
            }
            case Expressions.String stringExpression -> {
                return LLVMBuildGlobalStringPtr(builder, stringExpression.value(), stringExpression.value());
            }
            case Expressions.Integer integerExpression -> {
                LLVMTypeRef i128type = LLVMInt128TypeInContext(this.ctx);
                return LLVMConstInt(i128type, integerExpression.value().longValueExact(), 1);
            }
            case Expressions.Float floatExpression -> {
                LLVMTypeRef f128type = LLVMFP128TypeInContext(this.ctx);
                return LLVMConstReal(f128type, floatExpression.value().doubleValue());
            }
            case Expressions.Char charExpression -> {
                LLVMTypeRef i8Type = LLVMInt8TypeInContext(this.ctx);
                return LLVMConstInt(i8Type, charExpression.value(), 0);
            }
            case Expressions.Boolean boolExpression -> {
                LLVMTypeRef i1Type = LLVMInt1TypeInContext(this.ctx);
                return LLVMConstInt(i1Type, boolExpression.value() ? 1 : 0, 0);
            }
            case Expressions.Addition additionExpression -> {
                return LLVMBuildAdd(this.builder, addExpression(block, additionExpression.left()), addExpression(block, additionExpression.right()), getStatementCount() + "_add");
            }
            case Expressions.Subtraction subtractionExpression -> {
                return LLVMBuildSub(this.builder, addExpression(block, subtractionExpression.left()), addExpression(block, subtractionExpression.right()), getStatementCount() + "_sub");
            }
            case Expressions.Multiply multiplyExpression -> {
                return LLVMBuildMul(this.builder, addExpression(block, multiplyExpression.left()), addExpression(block, multiplyExpression.right()), getStatementCount() + "_mul");
            }
            case Expressions.Divide divideExpression -> {
                return LLVMBuildSDiv(this.builder, addExpression(block, divideExpression.left()), addExpression(block, divideExpression.right()), getStatementCount() + "_div");
            }
            case Expressions.Modulo moduloExpression -> {
                return LLVMBuildSRem(this.builder, addExpression(block, moduloExpression.left()), addExpression(block, moduloExpression.right()), getStatementCount() + "_mod");
            }
            case Expressions.Variable variableExpression -> {
                LLVMValueRef value;
                String name = variableExpression.name();
                if (locals.containsKey(name)) {
                    value = locals.get(name);
                } else {
                    throw new IllegalStateException("Undeclared variable of name: " + name);
                }
                return LLVMBuildLoad(this.builder, value, name);
            }
            case Expressions.Assignment assignmentExpression -> {
                LLVMValueRef value;
                if (assignmentExpression.left() instanceof Expressions.Variable(String name)) {
                    if (locals.containsKey(name)) {
                        value = locals.get(name);
                    } else {
                        throw new IllegalStateException("Undeclared variable of name: " + name);
                    }
                } else {
                    throw new IllegalStateException("Unimplemented assignment for LHS: " + assignmentExpression.left().getClass().getSimpleName());
                }
                LLVMValueRef rhsValue = addExpression(block, assignmentExpression.right());
                LLVMTypeRef valueType = LLVMGetAllocatedType(value);
                LLVMTypeRef rhsType = LLVMTypeOf(rhsValue);

                LLVMValueRef cast = rhsValue;
                if (!rhsType.equals(valueType)) {
                    switch (LLVMGetTypeKind(valueType)) {
                        case LLVMIntegerTypeKind -> {
                            cast = LLVMBuildIntCast(this.builder, rhsValue, valueType, name + "." + getStatementCount() + ".cast");
                        }
                        case LLVMFloatTypeKind, LLVMDoubleTypeKind, LLVMHalfTypeKind -> {
                            cast = LLVMBuildFPCast(this.builder, rhsValue, valueType, name + "." + getStatementCount() + ".cast");
                        }
                        default -> {
                            throw new IllegalStateException("Unimplemented implicit cast for RHS type kind " +  LLVMGetTypeKind(valueType));
                        }
                    }

                }
                return LLVMBuildStore(this.builder, cast, value);
            }
            default -> throw new IllegalStateException("Unimplemented expression type: " + expression.getClass().getSimpleName());
        }
    }

    private boolean validate() {
        if (LLVMVerifyModule(this.module, LLVMPrintMessageAction, error) != 0) {
            System.err.println("Failed to validate module: " + error.getString());
            LLVMDisposeMessage(error);
            return false;
        }
        return true;
    }

    private void write() {
        System.out.println(LLVMPrintModuleToString(this.module).getString());
        LLVMPrintModuleToFile(this.module, this.output.toString(), error);
    }

    @Override
    public void close() {
        LLVMDisposeBuilder(this.builder);
        LLVMDisposeModule(this.module);
        LLVMContextDispose(this.ctx);
    }

    private void resetStatementCount() {
        this.statements = 0;
    }

    private int getStatementCount() {
        return this.statements++;
    }

    private LLVMTypeRef getLLVMType(TypeReference type) {
        switch (type) {
            case PrimitiveTypeReference(Primitive primitive) -> {
                switch (primitive) {
                    case I8, U8 -> {
                        return LLVMInt8TypeInContext(this.ctx);
                    }
                    case I16, U16 -> {
                        return LLVMInt16TypeInContext(this.ctx);
                    }
                    case I32, U32 -> {
                        return LLVMInt32TypeInContext(this.ctx);
                    }
                    case I64, U64 -> {
                        return LLVMInt64TypeInContext(this.ctx);
                    }
                    case I128, U128 -> {
                        return LLVMInt128TypeInContext(this.ctx);
                    }
                    case BOOL -> {
                        return LLVMInt1TypeInContext(this.ctx);
                    }
                    case F32 -> {
                        return LLVMFloatTypeInContext(this.ctx);
                    }
                    case F64 -> {
                        return LLVMDoubleTypeInContext(this.ctx);
                    }
                    case VOID -> {
                        return LLVMVoidTypeInContext(this.ctx);
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
