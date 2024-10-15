package pro.trevor.joe.program.code.expression;

import pro.trevor.joe.program.code.Expression;

public record ArrayIndexExpression(Expression array, Expression index) implements Expression {
}
