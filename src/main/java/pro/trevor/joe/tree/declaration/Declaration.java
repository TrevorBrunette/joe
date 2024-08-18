package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Node;
import pro.trevor.joe.tree.Symbol;

public abstract class Declaration extends Node {

    private final Symbol identifier;

    public Declaration(Location location, Symbol identifier) {
        super(location);
        this.identifier = identifier;
    }

    public Symbol getIdentifier() {
        return identifier;
    }
}
