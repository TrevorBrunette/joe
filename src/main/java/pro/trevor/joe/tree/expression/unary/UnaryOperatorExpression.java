package pro.trevor.joe.tree.expression.unary;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;

public abstract class UnaryOperatorExpression extends Expression {

    protected final Expression operand;

    public UnaryOperatorExpression(Location location, Expression operand) {
        super(location);
        this.operand = operand;
    }

    public Expression getOperand() {
        return operand;
    }

    @Override
    public boolean isAssignable() {
        return false;
    }
}
