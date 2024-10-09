package pro.trevor.joe.parser.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.IStatement;
import pro.trevor.joe.parser.tree.Node;

public abstract class Expression extends Node implements IStatement {
    public Expression(Location location) {
        super(location);
    }

    public abstract boolean isAssignable();
}
