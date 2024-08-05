package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Node;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.type.Type;

public abstract class Declaration extends Node {

    private Type type;
    private final Symbol identifier;
    private final Declaration parent;

    public Declaration(Location location, Type type, Symbol identifier, Declaration parent) {
        super(location);
        this.type = type;
        this.identifier = identifier;
        this.parent = parent;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Symbol getIdentifier() {
        return identifier;
    }

    public Declaration getParent() {
        return parent;
    }
}
