package pro.trevor.joe.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.type.Type;

import java.util.List;

public class MethodInvocationExpression extends Expression {

    private final Type type;
    private final Expression method;
    private final List<Expression> parameters;

    public MethodInvocationExpression(Location location, Type type, Expression method, List<Expression> parameters) {
        super(location);
        this.type = type;
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
    public Type type() {
        return type;
    }

    @Override
    public boolean isAssignable() {
        return false;
    }
}
