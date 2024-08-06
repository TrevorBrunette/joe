package pro.trevor.joe.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.IStatement;
import pro.trevor.joe.tree.Node;
import pro.trevor.joe.tree.Symbol;

public class VariableDeclarationStatement extends Node implements IStatement {

    private final Symbol type;
    private final Symbol identifier;

    public VariableDeclarationStatement(Location location, Symbol type, Symbol identifier) {
        super(location);
        this.type = type;
        this.identifier = identifier;
    }

    public Symbol getType() {
        return type;
    }

    public Symbol getIdentifier() {
        return identifier;
    }
}
