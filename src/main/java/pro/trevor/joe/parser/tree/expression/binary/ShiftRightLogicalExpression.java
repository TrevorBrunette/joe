package pro.trevor.joe.parser.tree.expression.binary;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.expression.Associativity;
import pro.trevor.joe.parser.tree.expression.Expression;

public class ShiftRightLogicalExpression extends BinaryOperatorExpression {
    public ShiftRightLogicalExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location, leftOperand, rightOperand, 10, Associativity.LEFT_TO_RIGHT);
    }
}
