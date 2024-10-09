package pro.trevor.joe.parser.tree.expression;

import pro.trevor.joe.lexer.Location;

import java.util.List;

public class MethodInvocationExpression extends Expression {

    private final Expression method;
    private final List<Expression> parameters;

    public MethodInvocationExpression(Location location, Expression method, List<Expression> parameters) {
        super(location);
        this.method = method;
        this.parameters = parameters;
    }

    public Expression getMethod() {
        return method;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    @Override
    public boolean isAssignable() {
        return false;
    }
}
