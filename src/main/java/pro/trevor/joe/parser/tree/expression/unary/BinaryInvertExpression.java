package pro.trevor.joe.parser.tree.expression.unary;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.expression.Expression;

public class BinaryInvertExpression extends UnaryOperatorExpression {
    public BinaryInvertExpression(Location location, Expression operand) {
        super(location, operand);
    }
}
