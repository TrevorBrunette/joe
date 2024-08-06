package pro.trevor.joe.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.type.Type;

public class VariableInitializationStatement extends VariableDeclarationStatement {

    private final Expression expression;

    public VariableInitializationStatement(Location location, Symbol type, Symbol identifier, Expression expression) {
        super(location, type, identifier);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
