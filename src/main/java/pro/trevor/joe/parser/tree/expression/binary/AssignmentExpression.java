package pro.trevor.joe.parser.tree.expression.binary;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.expression.Associativity;
import pro.trevor.joe.parser.tree.expression.Expression;

public class AssignmentExpression extends BinaryOperatorExpression {
    public AssignmentExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location, leftOperand, rightOperand, 1, Associativity.RIGHT_TO_LEFT);
    }
}
