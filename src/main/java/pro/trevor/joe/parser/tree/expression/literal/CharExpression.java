package pro.trevor.joe.parser.tree.expression.literal;

import pro.trevor.joe.lexer.Location;

public class CharExpression extends LiteralExpression {

    private final String value;

    public CharExpression(Location location, String value) {
        super(location);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
