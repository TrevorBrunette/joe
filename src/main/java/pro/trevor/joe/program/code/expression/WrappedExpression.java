package pro.trevor.joe.program.code.expression;

import pro.trevor.joe.program.code.Expression;

public record WrappedExpression(Expression expression) implements Expression {
}
