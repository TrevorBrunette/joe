package pro.trevor.joe.parser.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.IStatement;
import pro.trevor.joe.parser.tree.Node;
import pro.trevor.joe.parser.tree.expression.Expression;

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
