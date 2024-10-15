package pro.trevor.joe.program.code.statement;

import pro.trevor.joe.program.code.Expression;
import pro.trevor.joe.program.code.Statement;

public class IfStatement implements Statement {

    private final Expression condition;
    private final Statement ifTrue;
    private final Statement ifFalse;

    public IfStatement(Expression condition, Statement ifTrue, Statement ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public IfStatement(Expression condition, Statement ifTrue) {
        this(condition, ifTrue, null);
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getIfTrue() {
        return ifTrue;
    }

    boolean hasFalseBranch() {
        return ifFalse != null;
    }

    public Statement getIfFalse() {
        return ifFalse;
    }
}
