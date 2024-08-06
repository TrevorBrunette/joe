package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Node;
import pro.trevor.joe.tree.Symbol;

public abstract class Declaration extends Node {

    private final Symbol identifier;
    private final Declaration parent;

    public Declaration(Location location, Symbol identifier, Declaration parent) {
        super(location);
        this.identifier = identifier;
        this.parent = parent;
    }

    public Symbol getIdentifier() {
        return identifier;
    }

    public Declaration getParent() {
        return parent;
    }
}
