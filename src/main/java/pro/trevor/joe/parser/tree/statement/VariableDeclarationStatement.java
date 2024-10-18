package pro.trevor.joe.parser.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.IStatement;
import pro.trevor.joe.parser.tree.Node;
import pro.trevor.joe.parser.tree.Type;

public class VariableDeclarationStatement extends Node implements IStatement {

    private final Type type;
    private final String identifier;

    public VariableDeclarationStatement(Location location, Type type, String identifier) {
        super(location);
        this.type = type;
        this.identifier = identifier;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }
}
