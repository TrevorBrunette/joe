package pro.trevor.joe.parser.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Symbol;

import java.util.List;

public class ObjectInstantiationExpression extends Expression {

    private final Symbol type;
    private final List<Expression> parameters;

    public ObjectInstantiationExpression(Location location, Symbol type, List<Expression> parameters) {
        super(location);
        this.type = type;
        this.parameters = parameters;
    }

    public Symbol getType() {
        return type;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    @Override
    public boolean isAssignable() {
        return false;
    }
}
