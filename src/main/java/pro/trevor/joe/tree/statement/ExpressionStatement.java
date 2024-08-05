package pro.trevor.joe.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.IStatement;
import pro.trevor.joe.tree.Node;
import pro.trevor.joe.tree.expression.Expression;

public class ExpressionStatement extends Node implements IStatement {

    private final Expression expression;

    public ExpressionStatement(Location location, Expression expression) {
        super(location);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
