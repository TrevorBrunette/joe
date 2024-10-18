package pro.trevor.joe.parser.tree.expression;

import pro.trevor.joe.lexer.Location;

public class VariableExpression extends Expression {

    private final String identifier;

    public VariableExpression(Location location, String identifier) {
        super(location);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
