package pro.trevor.joe.parser.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.IStatement;
import pro.trevor.joe.parser.tree.Node;
import pro.trevor.joe.parser.tree.Symbol;
import pro.trevor.joe.parser.tree.Type;

public class VariableDeclarationStatement extends Node implements IStatement {

    private final Type type;
    private final Symbol identifier;

    public VariableDeclarationStatement(Location location, Type type, Symbol identifier) {
        super(location);
        this.type = type;
        this.identifier = identifier;
    }

    public Type getType() {
        return type;
    }

    public Symbol getIdentifier() {
        return identifier;
    }
}
