package pro.trevor.joe.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.IStatement;
import pro.trevor.joe.tree.Node;
import pro.trevor.joe.tree.expression.type.Type;

public abstract class Expression extends Node implements IStatement {
    public Expression(Location location) {
        super(location);
    }

    public abstract Type type();

    public abstract boolean isAssignable();
}
