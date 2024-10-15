package pro.trevor.joe.program.code.statement;

import pro.trevor.joe.program.code.Expression;
import pro.trevor.joe.program.code.Statement;

public class ReturnStatement implements Statement {

    private final Expression toReturn;

    public ReturnStatement(Expression toReturn) {
        this.toReturn = toReturn;
    }

    public Expression getToReturn() {
        return toReturn;
    }
}
