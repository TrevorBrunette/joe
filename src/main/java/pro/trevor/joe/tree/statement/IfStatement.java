package pro.trevor.joe.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Node;
import pro.trevor.joe.tree.IStatement;
import pro.trevor.joe.tree.expression.Expression;

public class IfStatement extends Node implements IStatement {

    private final Expression condition;
    private final IStatement ifTrue;
    private final IStatement ifFalse;

    public IfStatement(Location location, Expression condition, IStatement ifTrue, IStatement ifFalse) {
        super(location);
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public IfStatement(Location location, Expression condition, IStatement ifTrue) {
        this(location, condition, ifTrue, null);
    }

    public Expression getCondition() {
        return condition;
    }

    public IStatement getIfTrue() {
        return ifTrue;
    }

    boolean hasFalseBranch() {
        return ifFalse != null;
    }

    public IStatement getIfFalse() {
        return ifFalse;
    }
}
