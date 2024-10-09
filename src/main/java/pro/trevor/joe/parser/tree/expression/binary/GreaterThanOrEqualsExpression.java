package pro.trevor.joe.parser.tree.expression.binary;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.expression.Associativity;
import pro.trevor.joe.parser.tree.expression.Expression;

public class GreaterThanOrEqualsExpression extends BinaryOperatorExpression {
    public GreaterThanOrEqualsExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location, leftOperand, rightOperand, 9, Associativity.LEFT_TO_RIGHT);
    }
}
