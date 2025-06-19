package pro.trevor.joe.program.code;

import pro.trevor.joe.program.Parameter;
import pro.trevor.joe.program.type.NamedTypeReference;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class Expressions {

    public sealed interface Expression permits AssignableExpression, YieldingExpression
    {}

    public sealed interface AssignableExpression extends Expression permits
            Tuple,
            Variable,
            VariableAccess
    {}

    public record Tuple(List<Expression> members) implements AssignableExpression {}
    public record Variable(java.lang.String name) implements AssignableExpression {}

    public sealed interface YieldingExpression extends Expression permits BinaryExpression, LiteralExpression, MiscExpression, ProgramExpression, UnaryExpression {}

    public sealed interface BinaryExpression extends YieldingExpression permits
            Addition,
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

    public record Addition(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record BinaryAnd(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record BinaryOr(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record BinaryXor(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record Divide(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record Equals(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record GreaterThan(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record GreaterThanOrEquals(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record LessThan(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record LessThanOrEquals(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record LogicalAnd(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record LogicalOr(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record LogicalXor(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record Modulo(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record Multiply(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record NotEquals(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record ShiftLeft(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record ShiftRight(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record ShiftRightLogical(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record Subtraction(YieldingExpression left, YieldingExpression right) implements BinaryExpression {}
    public record VariableAccess(YieldingExpression left, Variable right) implements BinaryExpression, AssignableExpression {}

    public sealed interface UnaryExpression extends YieldingExpression permits
            BinaryInvert,
            LogicalInvert,
            NumericalNegate
    {}

    public record BinaryInvert(Expression operand) implements UnaryExpression {}
    public record LogicalInvert(Expression operand) implements UnaryExpression {}
    public record NumericalNegate(Expression operand) implements UnaryExpression {}

    public sealed interface LiteralExpression extends YieldingExpression permits
            Boolean,
            Char,
            Float,
            Integer,
            String,
            Super,
            This,
            Null
    {}

    public record Boolean(boolean value) implements LiteralExpression {}
    public record Char(char value) implements LiteralExpression {}
    public record Float(BigDecimal value) implements LiteralExpression {}
    public record Integer(BigInteger value) implements LiteralExpression {}
    public record String(java.lang.String value) implements LiteralExpression {}
    public record Super(NamedTypeReference self) implements LiteralExpression {}
    public record This(NamedTypeReference self) implements LiteralExpression {}
    public record Null() implements LiteralExpression {}

    public sealed interface ProgramExpression extends YieldingExpression permits
            Block,
            If,
            IfElse
    {}

    public record Block(List<Statements.Statement> statements) implements ProgramExpression {}
    public record If(Expressions.Expression condition, Statements.Statement thenStatement) implements ProgramExpression {}
    public record IfElse(Expressions.Expression condition, Statements.Statement thenStatement, Statements.Statement elseStatement) implements ProgramExpression {}

    public sealed interface MiscExpression extends YieldingExpression permits
            ArrayIndex,
            Assignment,
            Closure,
            MethodInvocation,
            ObjectInstantiation,
            Wrapped
    {}

    public record ArrayIndex(Expression array, YieldingExpression index) implements MiscExpression {}
    public record Assignment(Expression left, YieldingExpression right) implements MiscExpression {}
    public record Closure(List<Parameter> parameters, Statements.Statement statement) implements MiscExpression {}
    public record MethodInvocation(Expression method, List<YieldingExpression> arguments) implements MiscExpression {}
    public record ObjectInstantiation(NamedTypeReference type, List<YieldingExpression> arguments) implements MiscExpression {}
    public record Wrapped(YieldingExpression expression) implements MiscExpression {}
}
