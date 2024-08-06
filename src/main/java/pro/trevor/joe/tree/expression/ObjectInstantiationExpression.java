package pro.trevor.joe.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.type.ClassType;
import pro.trevor.joe.tree.expression.type.Type;

import java.util.List;

public class ObjectInstantiationExpression extends Expression {

    private final ClassType type;
    private final List<Expression> parameters;

    public ObjectInstantiationExpression(Location location, ClassType type, List<Expression> parameters) {
        super(location);
        this.type = type;
        this.parameters = parameters;
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
