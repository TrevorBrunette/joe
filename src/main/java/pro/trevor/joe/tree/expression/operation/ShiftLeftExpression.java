package pro.trevor.joe.tree.expression.operation;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.Type;

public class ShiftLeftExpression extends BinaryOperatorExpression {
    public ShiftLeftExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location, leftOperand, rightOperand, 10, Associativity.LEFT_TO_RIGHT);
    }

    @Override
    public Type type() {
        return leftOperand.type();
    }
}
