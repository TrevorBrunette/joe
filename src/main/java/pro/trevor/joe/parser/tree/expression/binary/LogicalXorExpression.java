package pro.trevor.joe.parser.tree.expression.binary;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.expression.Associativity;
import pro.trevor.joe.parser.tree.expression.Expression;

public class LogicalXorExpression extends BinaryOperatorExpression {
    public LogicalXorExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location, leftOperand, rightOperand, 3, Associativity.LEFT_TO_RIGHT);
    }
}
