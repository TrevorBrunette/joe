package pro.trevor.joe.program.code;

import pro.trevor.joe.program.type.TypeReference;

import java.util.List;

public class Statements {

    public sealed interface Statement permits
            Block,
            Empty,
            Expression,
            If,
            Return,
            VariableDeclaration,
            VariableInitialization,
            While
    {}

    public record Block(List<Statement> statements) implements Statement {}
    public record Empty() implements Statement {}
    public record Expression(Expression expression) implements Statement {}
    public record If(Expression condition, Statement thenStatement, Statement elseStatement) implements Statement {}
    public record Return(Expression value) implements Statement {}
    public record VariableDeclaration(TypeReference type, String name) implements Statement {}
    public record VariableInitialization(TypeReference type, String name, Expression value) implements Statement {}
    public record While(Expression condition, Statement body) implements Statement {}


}
