package pro.trevor.joe.parser.tree.expression;

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
    public boolean isAssignable() {
        return false;
    }
}
