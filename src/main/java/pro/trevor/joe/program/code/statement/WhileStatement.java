package pro.trevor.joe.program.code.statement;

import pro.trevor.joe.program.code.Expression;
import pro.trevor.joe.program.code.Statement;

public class WhileStatement implements Statement {

    private final Expression condition;
    private final Statement statement;

    public WhileStatement(Expression condition, Statement statement) {
        this.condition = condition;
        this.statement = statement;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getStatement() {
        return statement;
    }
}
