package pro.trevor.joe.parser.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Symbol;

public class VariableExpression extends Expression {

    private final Symbol identifier;

    public VariableExpression(Location location, Symbol identifier) {
        super(location);
        this.identifier = identifier;
    }

    public Symbol getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isAssignable() {
        return true;
    }
}
