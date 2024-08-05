package pro.trevor.joe.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Node;
import pro.trevor.joe.tree.IStatement;
import pro.trevor.joe.tree.expression.Expression;

public class WhileStatement extends Node implements IStatement {

    private final Expression condition;
    private final IStatement statement;

    public WhileStatement(Location location, Expression condition, IStatement statement) {
        super(location);
        this.condition = condition;
        this.statement = statement;
    }

    public Expression getCondition() {
        return condition;
    }

    public IStatement getStatement() {
        return statement;
    }
}
