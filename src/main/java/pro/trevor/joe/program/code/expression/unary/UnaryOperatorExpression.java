package pro.trevor.joe.program.code.expression.unary;

import pro.trevor.joe.program.code.Expression;

public abstract class UnaryOperatorExpression implements Expression {

    protected final Expression operand;

    public UnaryOperatorExpression(Expression operand) {
        this.operand = operand;
    }

    public Expression getOperand() {
        return operand;
    }
}
