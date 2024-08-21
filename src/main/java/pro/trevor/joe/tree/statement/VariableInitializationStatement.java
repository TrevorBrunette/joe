package pro.trevor.joe.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.Type;
import pro.trevor.joe.tree.expression.Expression;

public class VariableInitializationStatement extends VariableDeclarationStatement {

    private final Expression expression;

    public VariableInitializationStatement(Location location, Type type, Symbol identifier, Expression expression) {
        super(location, type, identifier);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
