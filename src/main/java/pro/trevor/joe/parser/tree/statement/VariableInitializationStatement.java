package pro.trevor.joe.parser.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Symbol;
import pro.trevor.joe.parser.tree.Type;
import pro.trevor.joe.parser.tree.expression.Expression;

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
