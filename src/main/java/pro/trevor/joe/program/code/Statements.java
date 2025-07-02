package pro.trevor.joe.program.code;

import pro.trevor.joe.program.type.TypeReference;

import java.util.List;

public class Statements {

    public sealed interface Statement permits
            Block,
            Empty,
            Expression,
            For,
            If,
            IfElse,
            Return,
            VariableDeclaration,
            VariableInitialization,
            While,
            Yield
    {}

    public record Block(List<Statement> statements) implements Statements.Statement {}
    public record Empty() implements Statement {}
    public record Expression(Expressions.Expression expression) implements Statement {}
    public record For(Expressions.AssignableExpression assignment, Expressions.YieldingExpression expression) implements Statement {}
    public record If(Expressions.Expression condition, Statement then) implements Statement {}
    public record IfElse(Expressions.Expression condition, Statement then, Statement elseStatement) implements Statement {}
    public record Return(Expressions.Expression value) implements Statement {}
    public record VariableDeclaration(TypeReference type, String name) implements Statement {}
    public record VariableInitialization(TypeReference type, String name, Expressions.Expression value) implements Statement {}
    public record While(Expressions.Expression condition, Statement body) implements Statement {}
    public record Yield(Expressions.Expression expression) implements Statement {}

}
