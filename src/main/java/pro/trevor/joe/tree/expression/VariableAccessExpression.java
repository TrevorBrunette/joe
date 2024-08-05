package pro.trevor.joe.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class VariableAccessExpression extends Expression {

    private final Expression leftExpression;
    private final Symbol variableName;
    private final Type type;

    public VariableAccessExpression(Location location, Expression leftExpression, Symbol variableName, Type type) {
        super(location);
        this.leftExpression = leftExpression;
        this.variableName = variableName;
        this.type = type;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Symbol getVariableName() {
        return variableName;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public boolean isAssignable() {
        return true;
    }
}
