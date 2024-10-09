package pro.trevor.joe.parser.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Node;
import pro.trevor.joe.parser.tree.IStatement;

public class EmptyStatement extends Node implements IStatement {
    public EmptyStatement(Location location) {
        super(location);
    }
}
