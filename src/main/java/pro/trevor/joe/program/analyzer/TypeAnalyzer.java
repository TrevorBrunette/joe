package pro.trevor.joe.program.analyzer;

import pro.trevor.joe.program.File;
import pro.trevor.joe.program.code.Expressions;
import pro.trevor.joe.program.type.ArrayTypeReference;
import pro.trevor.joe.program.type.Primitive;
import pro.trevor.joe.program.type.PrimitiveTypeReference;
import pro.trevor.joe.program.type.TypeReference;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public class TypeAnalyzer {

    private final TypeContext ctx;
    private final Map<Expressions.YieldingExpression, TypeReference> types;

    public TypeAnalyzer(File file) {
        this.ctx = new TypeContext(file);
        this.types = new IdentityHashMap<>();
    }

    public TypeContext getContext() {
        return ctx;
    }

    public void analyze(Expressions.Expression expression) {
        if (!(expression instanceof Expressions.YieldingExpression yielding)) {
            return;
        }
        try {
            analyzeYielding(yielding);
        } catch (AnalyzeException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Optional<TypeReference> getType(Expressions.Expression expression) {
        return Optional.ofNullable(types.get(expression));
    }

    private TypeReference analyzeYielding(Expressions.YieldingExpression expression) throws AnalyzeException {
        if (types.containsKey(expression)) {
            return types.get(expression);
        }
        switch (expression) {
            case Expressions.Wrapped exp -> {
                TypeReference type = analyzeYielding(exp.expression());
                this.types.put(exp, type);
                return type;
            }
            case Expressions.Boolean exp -> {
                PrimitiveTypeReference type = new PrimitiveTypeReference(Primitive.BOOL);
                this.types.put(exp, type);
                return type;
            }
            case Expressions.Char exp -> {
                PrimitiveTypeReference type = new PrimitiveTypeReference(Primitive.U8);
                this.types.put(exp, type);
                return type;
            }
            case Expressions.Float exp -> {
                PrimitiveTypeReference type;
                if (exp.value().precision() > Float.PRECISION) {
                    type = new PrimitiveTypeReference(Primitive.F64);
                } else {
                    type = new PrimitiveTypeReference(Primitive.F32);
                }
                this.types.put(exp, type);
                return type;
            }
            case Expressions.Integer exp -> {
                int bytes = exp.value().toByteArray().length;
                PrimitiveTypeReference type;
                if (bytes <= Integer.BYTES) {
                    type = new PrimitiveTypeReference(Primitive.I32);
                } else if (bytes <= Long.BYTES) {
                    type = new PrimitiveTypeReference(Primitive.I64);
                } else {
                    type = new PrimitiveTypeReference(Primitive.I128);
                }
                this.types.put(exp, type);
                return type;
            }
            case Expressions.String exp -> {
                PrimitiveTypeReference underlyingType = new PrimitiveTypeReference(Primitive.U8);
                ArrayTypeReference stringType = new ArrayTypeReference(underlyingType);
                this.types.put(exp, stringType);
                return stringType;
            }
            case Expressions.Addition exp -> {
                return handleArithmetic(exp);
            }
            case Expressions.BinaryAnd exp -> {
                return handleBitwise(exp);
            }
            case Expressions.BinaryOr exp -> {
                return handleBitwise(exp);
            }
            case Expressions.BinaryXor exp -> {
                return handleBitwise(exp);
            }
            case Expressions.Divide exp -> {
                return handleArithmetic(exp);
            }
            case Expressions.Equals exp -> {
                return handleCompare(exp);
            }
            case Expressions.GreaterThan exp -> {
                return handleCompare(exp);
            }
            case Expressions.GreaterThanOrEquals exp -> {
                return handleCompare(exp);
            }
            case Expressions.LessThan exp -> {
                return handleCompare(exp);
            }
            case Expressions.LessThanOrEquals exp -> {
                return handleCompare(exp);
            }
            case Expressions.LogicalAnd exp -> {
                return handleBoolean(exp);
            }
            case Expressions.LogicalOr exp -> {
                return handleBoolean(exp);
            }
            case Expressions.LogicalXor exp -> {
                return handleBoolean(exp);
            }
            case Expressions.Modulo exp -> {
                return handleArithmetic(exp);
            }
            case Expressions.Multiply exp -> {
                return handleArithmetic(exp);
            }
            case Expressions.NotEquals exp -> {
                return handleCompare(exp);
            }
            case Expressions.ShiftLeft exp -> {
                return handleShift(exp);
            }
            case Expressions.ShiftRight exp -> {
                return handleShift(exp);
            }
            case Expressions.ShiftRightLogical exp -> {
                return handleShift(exp);
            }
            case Expressions.Subtraction exp -> {
                return handleArithmetic(exp);
            }
            case Expressions.NumericalNegate exp -> {
                TypeReference type = analyzeYielding(exp.operand());
                this.types.put(exp, type);
                return type;
            }
            case Expressions.BinaryInvert exp -> {
                TypeReference type = analyzeYielding(exp.operand());
                this.types.put(exp, type);
                return type;
            }
            case Expressions.LogicalInvert exp -> {
                return new PrimitiveTypeReference(Primitive.BOOL);
            }
            case Expressions.ArrayIndex exp -> {
                TypeReference type = new ArrayTypeReference(analyzeYielding(exp.array()));
                this.types.put(exp, type);
                return type;
            }
            case Expressions.ArrayInstantiation exp -> {
                this.types.put(exp, new ArrayTypeReference(exp.type()));
                return exp.type();
            }
            case Expressions.Assignment exp -> {
                TypeReference type = analyzeYielding(exp.right());
                this.types.put(exp, type);
                return type;
            }
            case Expressions.Variable exp -> {
                Optional<TypeReference> maybeType = this.ctx.getLocalType(exp.name());
                if (maybeType.isPresent()) {
                    TypeReference type = maybeType.get();
                    this.types.put(exp, type);
                    return type;
                } else {
                    throw new IllegalStateException("Undeclared variable: " + exp.name());
                }
            }
            case Expressions.MethodInvocation exp -> {
                // TODO implement
                return null;
            }
            default -> throw new IllegalStateException("Unexpected value: " + expression);
        }
    }

    private TypeReference handleArithmetic(Expressions.BinaryExpression expression) throws AnalyzeException {
        TypeReference expLeft = analyzeYielding(expression.left());
        TypeReference expRight = analyzeYielding(expression.right());
        if (expLeft instanceof PrimitiveTypeReference(Primitive left) && left.isNumber() &&
                expRight instanceof PrimitiveTypeReference(Primitive right) && right.isNumber()) {
            TypeReference result = new PrimitiveTypeReference(Primitive.arithmetic(left, right));
            this.types.put(expression, result);
            return result;
        } else {
            throw new AnalyzeException("Unsupported operand type(s)");
        }
    }

    private TypeReference handleBitwise(Expressions.BinaryExpression expression) throws AnalyzeException {
        TypeReference expLeft = analyzeYielding(expression.left());
        TypeReference expRight = analyzeYielding(expression.right());
        if (expLeft instanceof PrimitiveTypeReference left && left.primitive().isInt() && expRight instanceof PrimitiveTypeReference right && right.primitive().isInt()) {
            TypeReference result = new PrimitiveTypeReference(Primitive.arithmetic(left.primitive(), right.primitive()));
            this.types.put(expression, result);
            return result;
        } else {
            throw new AnalyzeException("Unsupported operand type(s)");
        }
    }

    private TypeReference handleCompare(Expressions.BinaryExpression expression) throws AnalyzeException {
        TypeReference expLeft = analyzeYielding(expression.left());
        TypeReference expRight = analyzeYielding(expression.right());
        if (expLeft instanceof PrimitiveTypeReference(Primitive left) && left.isNumber()
                && expRight instanceof PrimitiveTypeReference(Primitive right) && right.isNumber()) {
            TypeReference result = new PrimitiveTypeReference(Primitive.BOOL);
            this.types.put(expression, result);
            return result;
        } else {
            throw new AnalyzeException("Unsupported operand type(s)");
        }
    }

    private TypeReference handleBoolean(Expressions.BinaryExpression expression) throws AnalyzeException {
        TypeReference expLeft = analyzeYielding(expression.left());
        TypeReference expRight = analyzeYielding(expression.right());
        if (expLeft instanceof PrimitiveTypeReference(Primitive left) && (left == Primitive.BOOL)
                && expRight instanceof PrimitiveTypeReference(Primitive right) && (right == Primitive.BOOL)) {
            TypeReference result = new PrimitiveTypeReference(Primitive.BOOL);
            this.types.put(expression, result);
            return result;
        } else {
            throw new AnalyzeException("Unsupported operand type(s)");
        }
    }

    private TypeReference handleShift(Expressions.BinaryExpression expression) throws AnalyzeException {
        TypeReference expLeft = analyzeYielding(expression.left());
        TypeReference expRight = analyzeYielding(expression.right());
        if (expLeft instanceof PrimitiveTypeReference left && left.primitive().isInt() && expRight instanceof PrimitiveTypeReference right && right.primitive().isInt()) {
            TypeReference result = new PrimitiveTypeReference(Primitive.arithmetic(left.primitive(), right.primitive()));
            this.types.put(expression, result);
            return result;
        } else {
            throw new AnalyzeException("Unsupported operand type(s)");
        }
    }

}
