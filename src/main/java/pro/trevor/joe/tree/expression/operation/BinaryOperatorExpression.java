package pro.trevor.joe.tree.expression.operation;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;

public abstract class BinaryOperatorExpression extends Expression {

    protected final Expression leftOperand;
    protected final Expression rightOperand;

    public BinaryOperatorExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location);
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public Expression getLeftOperand() {
        return leftOperand;
    }

    public Expression getRightOperand() {
        return rightOperand;
    }

    @Override
    public boolean isAssignable() {
        return false;
    }
}
