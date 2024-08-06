package pro.trevor.joe.tree.expression;

import pro.trevor.joe.tree.expression.type.Type;

public class WrappedExpression extends Expression {

    private final Expression expression;

    public WrappedExpression(Expression expression) {
        super(expression.location());
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public Type type() {
        return expression.type();
    }

    @Override
    public boolean isAssignable() {
        return false;
    }
}
