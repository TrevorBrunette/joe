package pro.trevor.joe.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.type.Type;

public class LocalVariableExpression extends Expression {

    private final Symbol identifier;
    private final Type type;

    public LocalVariableExpression(Location location, Symbol identifier, Type type) {
        super(location);
        this.identifier = identifier;
        this.type = type;
    }

    public Symbol getIdentifier() {
        return identifier;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public boolean isAssignable() {
        return true;
    }
}
