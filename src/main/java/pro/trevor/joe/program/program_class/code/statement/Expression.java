package pro.trevor.joe.program.program_class.code.statement;

import pro.trevor.joe.program.program_class.code.Statement;

public class Expression implements Statement {

    private final pro.trevor.joe.parser.tree.expression.Expression expression;

    public Expression(pro.trevor.joe.parser.tree.expression.Expression expression) {
        this.expression = expression;
    }

    public pro.trevor.joe.parser.tree.expression.Expression getExpression() {
        return expression;
    }
}
