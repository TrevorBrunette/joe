package pro.trevor.joe.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

import java.util.List;

public class MethodInvocationExpression extends Expression {

    private final Symbol method;
    private final List<Expression> parameters;

    public MethodInvocationExpression(Location location, Symbol method, List<Expression> parameters) {
        super(location);
        this.method = method;
        this.parameters = parameters;
    }

    public Symbol getMethod() {
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
