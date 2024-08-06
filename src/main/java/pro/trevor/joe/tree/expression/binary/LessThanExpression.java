package pro.trevor.joe.tree.expression.binary;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Associativity;
import pro.trevor.joe.tree.expression.Expression;

public class LessThanExpression extends BinaryOperatorExpression {
    public LessThanExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location, leftOperand, rightOperand, 9, Associativity.LEFT_TO_RIGHT);
    }
}
