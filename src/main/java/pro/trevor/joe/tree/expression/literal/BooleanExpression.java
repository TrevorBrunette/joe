package pro.trevor.joe.tree.expression.literal;

import pro.trevor.joe.lexer.Location;

public class BooleanExpression extends LiteralExpression {

    private final boolean value;

    public BooleanExpression(Location location, boolean value) {
        super(location);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
