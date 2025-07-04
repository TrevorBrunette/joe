package pro.trevor.joe.program.llvm;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;
import org.bytedeco.llvm.LLVM.LLVMBuilderRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;
import pro.trevor.joe.program.code.Expressions;
import pro.trevor.joe.program.type.ArrayTypeReference;
import pro.trevor.joe.program.type.Primitive;
import pro.trevor.joe.program.type.PrimitiveTypeReference;
import pro.trevor.joe.program.type.TypeReference;
import pro.trevor.joe.util.Pair;

import java.util.Optional;

import static org.bytedeco.llvm.global.LLVM.*;

public class ExpressionGenerator {

    private final Generator generator;
    private final StatementGenerator statementGenerator;

    public ExpressionGenerator(Generator generator, StatementGenerator statementGenerator) {
        this.generator = generator;
        this.statementGenerator = statementGenerator;
    }

    public LLVMValueRef addExpression(LLVMBasicBlockRef block, Expressions.Expression expression) {
        LLVMPositionBuilderAtEnd(generator.llvm.builder, block);
        generator.typeAnalyzer.analyze(expression);

        switch (expression) {
            case Expressions.MethodInvocation methodInvocation -> {
                return methodInvocationExpression(block, methodInvocation);
            }
            case Expressions.String stringExpression -> {
                return LLVMBuildGlobalStringPtr(generator.llvm.builder, stringExpression.value(), stringExpression.value());
            }
            case Expressions.Integer integerExpression -> {
                LLVMTypeRef i128type = LLVMInt128TypeInContext(generator.llvm.ctx);
                return LLVMConstInt(i128type, integerExpression.value().longValueExact(), 1);
            }
            case Expressions.Float floatExpression -> {
                LLVMTypeRef f128type = LLVMFP128TypeInContext(generator.llvm.ctx);
                return LLVMConstReal(f128type, floatExpression.value().doubleValue());
            }
            case Expressions.Char charExpression -> {
                LLVMTypeRef i8Type = LLVMInt8TypeInContext(generator.llvm.ctx);
                return LLVMConstInt(i8Type, charExpression.value(), 0);
            }
            case Expressions.Boolean boolExpression -> {
                LLVMTypeRef i1Type = LLVMInt1TypeInContext(generator.llvm.ctx);
                return LLVMConstInt(i1Type, boolExpression.value() ? 1 : 0, 0);
            }
            case Expressions.Addition additionExpression -> {
                return arithmetic(block, additionExpression, LLVM::LLVMBuildAdd, "add");
            }
            case Expressions.Subtraction subtractionExpression -> {
                return arithmetic(block, subtractionExpression, LLVM::LLVMBuildSub, "sub");
            }
            case Expressions.Multiply multiplyExpression -> {
                return arithmetic(block, multiplyExpression, LLVM::LLVMBuildMul, "mul");
            }
            case Expressions.Divide divideExpression -> {
                return arithmetic(block, divideExpression, LLVM::LLVMBuildSDiv, "div");
            }
            case Expressions.Modulo moduloExpression -> {
                return arithmetic(block, moduloExpression, LLVM::LLVMBuildSRem, "mod");
            }
            case Expressions.Equals equalsExpression -> {
                return compExpression(block, equalsExpression);
            }
            case Expressions.NotEquals notEqualsExpression -> {
                return compExpression(block, notEqualsExpression);
            }
            case Expressions.LessThan lessThanExpression -> {
                return compExpression(block, lessThanExpression);
            }
            case Expressions.LessThanOrEquals lessThanOrEqualsExpression -> {
                return compExpression(block, lessThanOrEqualsExpression);
            }
            case Expressions.GreaterThan greaterThanExpression -> {
                return compExpression(block, greaterThanExpression);
            }
            case Expressions.GreaterThanOrEquals greaterThanOrEqualsExpression -> {
                return compExpression(block, greaterThanOrEqualsExpression);
            }
            case Expressions.Variable variableExpression -> {
                return variableExpression(variableExpression);
            }
            case Expressions.Assignment assignmentExpression -> {
                return assignmentExpression(block, assignmentExpression);
            }
            case Expressions.ArrayIndex arrayIndexExpression -> {
                return arrayIndexExpression(block, arrayIndexExpression);
            }
            case Expressions.ArrayInstantiation arrayInstantiationExpression -> {
                return arrayInstantiationExpression(block, arrayInstantiationExpression);
            }
            default -> throw new IllegalStateException("Unimplemented expression type: " + expression.getClass().getSimpleName());
        }
    }

    private LLVMValueRef methodInvocationExpression(LLVMBasicBlockRef block, Expressions.MethodInvocation methodInvocationExpression) {
        String name = ((Expressions.Variable) methodInvocationExpression.method()).name();
        LLVMValueRef fn = LLVMGetNamedFunction(generator.llvm.module, name);
        LLVMTypeRef fnType = LLVMGetGEPSourceElementType(fn);

        int fnParams = LLVMCountParamTypes(fnType);
        int providedParams = methodInvocationExpression.arguments().size();
        PointerPointer<LLVMTypeRef> paramTypesPtr = new PointerPointer<>(new LLVMTypeRef[fnParams]);
        LLVMGetParamTypes(fnType, paramTypesPtr);
        LLVMValueRef[] arguments = new LLVMValueRef[providedParams];

        if (LLVMIsFunctionVarArg(fnType) != 0) {
            if (fnParams <= providedParams) {
                for (int i = 0; i < fnParams; ++i) {
                    LLVMTypeRef paramType = paramTypesPtr.get(LLVMTypeRef.class, i);
                    arguments[i] = Util.cast(generator, addExpression(block, methodInvocationExpression.arguments().get(i)), paramType);
                }
                for (int i = fnParams; i < providedParams; ++i) {
                    arguments[i] = addExpression(block, methodInvocationExpression.arguments().get(i));
                }
            } else {
                throw new IllegalStateException("Incorrect number of arguments for var-arg function " + methodInvocationExpression.method() + "; expected at least " + fnParams + ", found " + providedParams);
            }
        } else {
            if (fnParams == providedParams) {
                for (int i = 0; i < fnParams; ++i) {
                    LLVMTypeRef paramType = paramTypesPtr.get(LLVMTypeRef.class, i);
                    arguments[i] = Util.cast(generator, addExpression(block, methodInvocationExpression.arguments().get(i)), paramType);
                }
            } else {
                throw new IllegalStateException("Incorrect number of arguments for function " + methodInvocationExpression.method() + "; expected " + fnParams + ", found " + providedParams);
            }
        }

        PointerPointer<LLVMValueRef> args = new PointerPointer<>(arguments);
        return LLVMBuildCall2(generator.llvm.builder, fnType, fn, args, arguments.length, name + ".call." + statementGenerator.getStatementCount());
    }

    private LLVMValueRef compExpression(LLVMBasicBlockRef block, Expressions.BinaryExpression expression) {
        Expressions.YieldingExpression left = expression.left();
        Expressions.YieldingExpression right = expression.right();

        TypeReference leftType = generator.typeAnalyzer.getType(left).orElseThrow();
        TypeReference rightType = generator.typeAnalyzer.getType(right).orElseThrow();

        LLVMValueRef lhs = addExpression(block, expression.left());
        LLVMValueRef rhs = addExpression(block, expression.right());

        if (leftType instanceof PrimitiveTypeReference leftPrimitive && rightType instanceof PrimitiveTypeReference rightPrimitive) {
            Optional<Constants.CompOpCode> opCode = Constants.CompOpCode.opCodeFor(expression, leftPrimitive, leftPrimitive);
            if (opCode.isEmpty()) {
                throw new IllegalStateException("Illegal internal comparison state for expression type: " + expression.getClass().getSimpleName());
            }

            int opCodeInt = opCode.get().op;

            Primitive resultType = Primitive.arithmetic(leftPrimitive.primitive(), rightPrimitive.primitive());
            PrimitiveTypeReference resultTypeRef = new PrimitiveTypeReference(resultType);
            LLVMValueRef lhsCast = Util.cast(generator, lhs, generator.getLLVMType(resultTypeRef));
            LLVMValueRef rhsCast = Util.cast(generator, rhs, generator.getLLVMType(resultTypeRef));

            if (resultType.isFloat()) {
                return LLVMBuildFCmp(generator.llvm.builder, opCodeInt, lhsCast, rhsCast, "fcmp." + statementGenerator.getStatementCount());
            } else if (resultType.isInt()) {
                return LLVMBuildICmp(generator.llvm.builder, opCodeInt, lhsCast, rhsCast, "icmp." + statementGenerator.getStatementCount());
            } else {
                throw new IllegalStateException("Illegal result type for comparison " + resultType.name());
            }
        } else {
            throw new IllegalStateException("Comparison of non-primitives: " + leftType + " and " + rightType);
        }

    }

    private LLVMValueRef variableExpression(Expressions.Variable variableExpression) {
        LLVMValueRef value;
        String name = variableExpression.name();
        Optional<LLVMValueRef> maybeValue = generator.typeAnalyzer.getContext().getLocalStorage(name);
        if (maybeValue.isPresent()) {
            value = maybeValue.get();
        } else {
            throw new IllegalStateException("Undeclared variable: " + name);
        }
        return LLVMBuildLoad2(generator.llvm.builder, LLVMGetAllocatedType(value), value, name + "." + statementGenerator.getStatementCount());
    }

    private LLVMValueRef assignmentExpression(LLVMBasicBlockRef block, Expressions.Assignment assignmentExpression) {
        LLVMValueRef rhsValue = addExpression(block, assignmentExpression.right());
        if (assignmentExpression.left() instanceof Expressions.Variable(String name)) {
            Optional<LLVMValueRef> maybeValue = generator.typeAnalyzer.getContext().getLocalStorage(name);
            if (maybeValue.isPresent()) {
                LLVMValueRef storagePtr = maybeValue.get();
                LLVMTypeRef storageType = LLVMGetAllocatedType(storagePtr);
                LLVMValueRef castValue = Util.cast(generator, rhsValue, storageType);
                return LLVMBuildStore(generator.llvm.builder, castValue, storagePtr);
            } else {
                throw new IllegalStateException("Undeclared variable of name: " + name);
            }
        } else if (assignmentExpression.left() instanceof Expressions.ArrayIndex arrayIndexExpression) {
            Pair<TypeReference, LLVMValueRef> typeAndPtr = arrayIndex(block, arrayIndexExpression.array(), arrayIndexExpression.index());
            LLVMTypeRef elementType = generator.getLLVMType(typeAndPtr.getLeft());
            LLVMValueRef valuePtr = typeAndPtr.getRight();
            LLVMValueRef toStore = Util.cast(generator, rhsValue, elementType);
            return LLVMBuildStore(generator.llvm.builder, toStore, valuePtr);
        } else {
            throw new IllegalStateException("Unimplemented assignment for LHS: " + assignmentExpression.left().getClass().getSimpleName());
        }
    }

    private interface ArithmeticFunction {
        LLVMValueRef apply(LLVMBuilderRef builder, LLVMValueRef left, LLVMValueRef right, String name);
    }

    private LLVMValueRef arithmetic(LLVMBasicBlockRef block, Expressions.BinaryExpression expression, ArithmeticFunction function, String name) {
        LLVMValueRef left = addExpression(block, expression.left());
        LLVMValueRef right = addExpression(block, expression.right());

        TypeReference leftType = generator.typeAnalyzer.getType(expression.left()).orElseThrow();
        TypeReference rightType = generator.typeAnalyzer.getType(expression.right()).orElseThrow();

        if (leftType instanceof PrimitiveTypeReference leftPrimitive && rightType instanceof PrimitiveTypeReference rightPrimitive) {
            Primitive resultType = Primitive.arithmetic(leftPrimitive.primitive(), rightPrimitive.primitive());
            PrimitiveTypeReference resultTypeRef = new PrimitiveTypeReference(resultType);
            LLVMTypeRef resultLlvmType = generator.getLLVMType(resultTypeRef);
            LLVMValueRef leftCast = Util.cast(generator, left, resultLlvmType);
            LLVMValueRef rightCast = Util.cast(generator, right, resultLlvmType);
            return function.apply(generator.llvm.builder, leftCast, rightCast, name + "." + statementGenerator.getStatementCount());
        } else {
            throw new IllegalStateException("Arithmetic of non-primitives: " + leftType + " and " + rightType);
        }
    }

    private LLVMValueRef arrayIndexExpression(LLVMBasicBlockRef block, Expressions.ArrayIndex arrayIndexExpression) {
        Pair<TypeReference, LLVMValueRef> typeAndPtr = arrayIndex(block, arrayIndexExpression.array(), arrayIndexExpression.index());
        LLVMTypeRef elementType = generator.getLLVMType(typeAndPtr.getLeft());
        LLVMValueRef valuePtr = typeAndPtr.getRight();
        return LLVMBuildLoad2(generator.llvm.builder, elementType, valuePtr, "array.access." + statementGenerator.getStatementCount());
    }

    private LLVMValueRef arrayInstantiationExpression(LLVMBasicBlockRef block, Expressions.ArrayInstantiation arrayInstantiationExpression) {
        LLVMValueRef mallocFn = LLVMGetNamedFunction(generator.llvm.module, "malloc");
        LLVMTypeRef mallocFnType = LLVMGetGEPSourceElementType(mallocFn);
        LLVMTypeRef mallocFnArgType = generator.getLLVMType(Intrinsics.MALLOC_ARG);

        LLVMTypeRef arrayElementType = generator.getLLVMType(arrayInstantiationExpression.type());
        LLVMValueRef numElements = addExpression(block, arrayInstantiationExpression.size());
        LLVMValueRef numElementsCast = Util.cast(generator, numElements, mallocFnArgType);
        LLVMValueRef arrayElementTypeSize = LLVMConstInt(mallocFnArgType, LLVMABISizeOfType(generator.llvm.targetData, arrayElementType), 0);
        LLVMValueRef arraySize = LLVMBuildMul(generator.llvm.builder, arrayElementTypeSize, numElementsCast, "array.size." + statementGenerator.getStatementCount());

        LLVMValueRef mallocSize = Util.cast(generator, arraySize, mallocFnArgType);
        LLVMValueRef mallocResult = LLVMBuildCall2(generator.llvm.builder, mallocFnType, mallocFn, new PointerPointer<>(new LLVMValueRef[]{mallocSize}), 1, "array.malloc." + statementGenerator.getStatementCount());

        return LLVMBuildBitCast(generator.llvm.builder, mallocResult, LLVMPointerType(arrayElementType, 0), "array.cast." + statementGenerator.getStatementCount());
    }

    private Pair<TypeReference, LLVMValueRef> arrayIndex(LLVMBasicBlockRef block, Expressions.YieldingExpression array, Expressions.YieldingExpression index) {
        LLVMValueRef arrayPointer = addExpression(block, array);
        LLVMValueRef indexValue = addExpression(block, index);
        LLVMTypeRef ptrSize = generator.getLLVMType(generator.llvm.ptrSize);
        LLVMValueRef arrayPtrCast = Util.ptrToInt(generator, arrayPointer, ptrSize);
        LLVMValueRef indexCast = Util.cast(generator, indexValue, ptrSize);
        ArrayTypeReference arrayPtrTypeRef = (ArrayTypeReference) generator.typeAnalyzer.getType(array).get();
        LLVMTypeRef arrayElementType = generator.getLLVMType(arrayPtrTypeRef.contained());
        LLVMValueRef arrayElementTypeSize = LLVMConstInt(ptrSize, LLVMABISizeOfType(generator.llvm.targetData, arrayElementType), 0);
        LLVMValueRef indexOffset = LLVMBuildMul(generator.llvm.builder, arrayElementTypeSize, indexCast, "array.index." + statementGenerator.getStatementCount());
        LLVMValueRef arrayTargetValueSrc = LLVMBuildAdd(generator.llvm.builder, arrayPtrCast, indexOffset, "array.target." + statementGenerator.getStatementCount());
        return new Pair<>(arrayPtrTypeRef.contained(), Util.intToPtr(generator, arrayTargetValueSrc, LLVMTypeOf(arrayPointer)));
    }

}
