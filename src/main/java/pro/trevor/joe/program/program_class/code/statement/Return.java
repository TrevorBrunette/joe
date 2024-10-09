package pro.trevor.joe.program.program_class.code.statement;

import pro.trevor.joe.program.program_class.code.Expression;
import pro.trevor.joe.program.program_class.code.Statement;

public class Return implements Statement {

    private final Expression toReturn;

    public Return(Expression toReturn) {
        this.toReturn = toReturn;
    }

    public Expression getToReturn() {
        return toReturn;
    }
}
