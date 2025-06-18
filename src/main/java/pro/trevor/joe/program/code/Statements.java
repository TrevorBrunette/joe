package pro.trevor.joe.program.code;

import pro.trevor.joe.program.type.TypeReference;

public class Statements {

    public sealed interface Statement permits
            Empty,
            Expression,
            For,
            Return,
            VariableDeclaration,
            VariableInitialization,
            While,
            Yield
    {}

    public record Empty() implements Statement {}
    public record Expression(Expressions.Expression expression) implements Statement {}
    public record For(Expressions.AssignableExpression assignment, Expressions.YieldingExpression expression) implements Statement {}
    public record Return(Expressions.Expression value) implements Statement {}
    public record VariableDeclaration(TypeReference type, String name) implements Statement {}
    public record VariableInitialization(TypeReference type, String name, Expressions.Expression value) implements Statement {}
    public record While(Expressions.Expression condition, Statement body) implements Statement {}
    public record Yield(Expressions.Expression expression) implements Statement {}

}
