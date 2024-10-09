package pro.trevor.joe.parser.tree.expression;

import pro.trevor.joe.lexer.Location;

public class ArrayIndexExpression extends Expression {

    private final Expression array;
    private final Expression index;

    public ArrayIndexExpression(Location location, Expression array, Expression index) {
        super(location);
        this.array = array;
        this.index = index;
    }

    public Expression getArray() {
        return array;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public boolean isAssignable() {
        return true;
    }
}
