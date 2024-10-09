package pro.trevor.joe.program.program_class.code.statement;

import pro.trevor.joe.program.program_class.code.Expression;
import pro.trevor.joe.program.program_class.code.Statement;

public class While implements Statement {

    private final Expression condition;
    private final Statement statement;

    public While(Expression condition, Statement statement) {
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
