package pro.trevor.joe.program.program_class.code.statement;

import pro.trevor.joe.program.program_class.code.Expression;
import pro.trevor.joe.program.program_class.code.Statement;

public class If implements Statement {

    private final Expression condition;
    private final Statement ifTrue;
    private final Statement ifFalse;

    public If(Expression condition, Statement ifTrue, Statement ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public If(Expression condition, Statement ifTrue) {
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
