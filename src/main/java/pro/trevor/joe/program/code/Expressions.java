package pro.trevor.joe.program.code;

import pro.trevor.joe.program.Parameter;
import pro.trevor.joe.program.type.NamedTypeReference;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class Expressions {

    public sealed interface Expression permits BinaryExpression, LiteralExpression, MiscExpression, UnaryExpression
    {}

    public sealed interface BinaryExpression extends Expression permits
            Addition,
            Assignment,
            BinaryAnd,
            BinaryOr,
            BinaryXor,
            Divide,
            Equals,
            GreaterThan,
            GreaterThanOrEquals,
            LessThan,
            LessThanOrEquals,
            LogicalAnd,
            LogicalOr,
            LogicalXor,
            Modulo,
            Multiply,
            NotEquals,
            ShiftLeft,
            ShiftRight,
            ShiftRightLogical,
            Subtraction,
            VariableAccess
    {}

    public record Addition(Expression left, Expression right) implements BinaryExpression {}
    public record Assignment(Expression left, Expression right) implements BinaryExpression {}
    public record BinaryAnd(Expression left, Expression right) implements BinaryExpression {}
    public record BinaryOr(Expression left, Expression right) implements BinaryExpression {}
    public record BinaryXor(Expression left, Expression right) implements BinaryExpression {}
    public record Divide(Expression left, Expression right) implements BinaryExpression {}
    public record Equals(Expression left, Expression right) implements BinaryExpression {}
    public record GreaterThan(Expression left, Expression right) implements BinaryExpression {}
    public record GreaterThanOrEquals(Expression left, Expression right) implements BinaryExpression {}
    public record LessThan(Expression left, Expression right) implements BinaryExpression {}
    public record LessThanOrEquals(Expression left, Expression right) implements BinaryExpression {}
    public record LogicalAnd(Expression left, Expression right) implements BinaryExpression {}
    public record LogicalOr(Expression left, Expression right) implements BinaryExpression {}
    public record LogicalXor(Expression left, Expression right) implements BinaryExpression {}
    public record Modulo(Expression left, Expression right) implements BinaryExpression {}
    public record Multiply(Expression left, Expression right) implements BinaryExpression {}
    public record NotEquals(Expression left, Expression right) implements BinaryExpression {}
    public record ShiftLeft(Expression left, Expression right) implements BinaryExpression {}
    public record ShiftRight(Expression left, Expression right) implements BinaryExpression {}
    public record ShiftRightLogical(Expression left, Expression right) implements BinaryExpression {}
    public record Subtraction(Expression left, Expression right) implements BinaryExpression {}
    public record VariableAccess(Expression left, Expression right) implements BinaryExpression {}

    public sealed interface UnaryExpression extends Expression permits
            BinaryInvert,
            LogicalInvert,
            NumericalNegate
    {}

    public record BinaryInvert(Expression operand) implements UnaryExpression {}
    public record LogicalInvert(Expression operand) implements UnaryExpression {}
    public record NumericalNegate(Expression operand) implements UnaryExpression {}

    public sealed interface LiteralExpression extends Expression permits
            Boolean,
            Char,
            Float,
            Integer,
            String,
            Super,
            This
    {}

    public record Boolean(boolean value) implements LiteralExpression {}
    public record Char(char value) implements LiteralExpression {}
    public record Float(BigDecimal value) implements LiteralExpression {}
    public record Integer(BigInteger value) implements LiteralExpression {}
    public record String(String value) implements LiteralExpression {}
    public record Super(NamedTypeReference self) implements LiteralExpression {}
    public record This(NamedTypeReference self) implements LiteralExpression {}

    public sealed interface MiscExpression extends Expression permits
            ArrayIndex,
            Closure,
            MethodInvocation,
            ObjectInstantiation,
            Tuple,
            Variable,
            Wrapped
    {}

    public record ArrayIndex(Expression array, Expression index) implements MiscExpression {}
    public record Closure(List<Parameter> parameters, Statements.Statement statement) implements MiscExpression {}
    public record MethodInvocation(Expression method, List<Expression> arguments) implements MiscExpression {}
    public record ObjectInstantiation(NamedTypeReference type, List<Expression> arguments) implements MiscExpression {}
    public record Tuple(List<Expression> members) implements MiscExpression {}
    public record Variable(String name) implements MiscExpression {}
    public record Wrapped(Expression expression) implements MiscExpression {}
}
