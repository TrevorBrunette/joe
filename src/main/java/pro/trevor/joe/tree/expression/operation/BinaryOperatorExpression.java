package pro.trevor.joe.tree.expression.operation;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;

public abstract class BinaryOperatorExpression extends Expression {

    protected final Expression leftOperand;
    protected final Expression rightOperand;
    protected final int priority;
    protected final Associativity associativity;

    public BinaryOperatorExpression(Location location, Expression leftOperand, Expression rightOperand, int priority, Associativity associativity) {
        super(location);
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.priority = priority;
        this.associativity = associativity;
    }

    public Expression getLeftOperand() {
        return leftOperand;
    }

    public Expression getRightOperand() {
        return rightOperand;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean isAssignable() {
        return false;
    }
}
