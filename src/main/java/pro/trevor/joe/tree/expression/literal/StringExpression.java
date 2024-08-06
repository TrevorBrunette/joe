package pro.trevor.joe.tree.expression.literal;

import pro.trevor.joe.lexer.Location;

public class StringExpression extends LiteralExpression {

    private final String value;

    public StringExpression(Location location, String value) {
        super(location);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
