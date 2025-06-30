package pro.trevor.joe.parser.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Type;

public class ArrayInstantiationExpression extends Expression {

    private final Type type;
    private final Expression sizeExpression;

    public ArrayInstantiationExpression(Location location, Type type, Expression sizeExpression) {
        super(location);
        this.type = type;
        this.sizeExpression = sizeExpression;
    }

    public Type getType() {
        return type;
    }

    public Expression getSizeExpression() {
        return sizeExpression;
    }
}
