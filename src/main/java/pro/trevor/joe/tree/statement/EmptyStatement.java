package pro.trevor.joe.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Node;
import pro.trevor.joe.tree.IStatement;

public class EmptyStatement extends Node implements IStatement {
    public EmptyStatement(Location location) {
        super(location);
    }
}
