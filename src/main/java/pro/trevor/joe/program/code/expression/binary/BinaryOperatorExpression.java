package pro.trevor.joe.program.code.expression.binary;

import pro.trevor.joe.program.code.Expression;

public abstract class BinaryOperatorExpression implements Expression {

    protected final Expression leftOperand;
    protected final Expression rightOperand;

    public BinaryOperatorExpression(Expression leftOperand, Expression rightOperand) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public Expression getLeftOperand() {
        return leftOperand;
    }

    public Expression getRightOperand() {
        return rightOperand;
    }
}
