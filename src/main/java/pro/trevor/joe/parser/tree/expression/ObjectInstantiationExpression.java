package pro.trevor.joe.parser.tree.expression;

import pro.trevor.joe.lexer.Location;

import java.util.List;

public class ObjectInstantiationExpression extends Expression {

    private final String type;
    private final List<Expression> parameters;

    public ObjectInstantiationExpression(Location location, String type, List<Expression> parameters) {
        super(location);
        this.type = type;
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public List<Expression> getParameters() {
        return parameters;
    }
}
