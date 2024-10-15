package pro.trevor.joe.program.code.expression;

import pro.trevor.joe.program.code.Expression;

import java.util.List;

public record MethodInvocationExpression(Expression method, List<Expression> arguments) implements Expression {
}
