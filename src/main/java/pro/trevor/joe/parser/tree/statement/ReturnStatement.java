package pro.trevor.joe.parser.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.IStatement;
import pro.trevor.joe.parser.tree.Node;
import pro.trevor.joe.parser.tree.expression.Expression;

public class ReturnStatement extends Node implements IStatement {

    private final Expression toReturn;

    public ReturnStatement(Location location, Expression toReturn) {
        super(location);
        this.toReturn = toReturn;
    }

    public Expression getToReturn() {
        return toReturn;
    }
}
