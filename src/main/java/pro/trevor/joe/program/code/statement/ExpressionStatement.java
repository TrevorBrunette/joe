package pro.trevor.joe.program.code.statement;

import pro.trevor.joe.program.code.Statement;

public class ExpressionStatement implements Statement {

    private final pro.trevor.joe.parser.tree.expression.Expression expression;

    public ExpressionStatement(pro.trevor.joe.parser.tree.expression.Expression expression) {
        this.expression = expression;
    }

    public pro.trevor.joe.parser.tree.expression.Expression getExpression() {
        return expression;
    }
}
