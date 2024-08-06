package pro.trevor.joe.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

public class VariableAccessExpression extends Expression {

    private final Expression leftExpression;
    private final Symbol variableName;

    public VariableAccessExpression(Location location, Expression leftExpression, Symbol variableName) {
        super(location);
        this.leftExpression = leftExpression;
        this.variableName = variableName;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Symbol getVariableName() {
        return variableName;
    }

    @Override
    public boolean isAssignable() {
        return true;
    }
}
