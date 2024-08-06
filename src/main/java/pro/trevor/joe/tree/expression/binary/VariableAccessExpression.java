package pro.trevor.joe.tree.expression.binary;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Associativity;
import pro.trevor.joe.tree.expression.Expression;

public class VariableAccessExpression extends BinaryOperatorExpression {

    public VariableAccessExpression(Location location, Expression leftExpression, Expression rightExpression) {
        super(location, leftExpression, rightExpression, 16, Associativity.LEFT_TO_RIGHT);
    }

    @Override
    public boolean isAssignable() {
        return true;
    }
}
