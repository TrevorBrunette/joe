package pro.trevor.joe.tree.expression.unary;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;

public class LogicalInvertExpression extends UnaryOperatorExpression {
    public LogicalInvertExpression(Location location, Expression operand) {
        super(location, operand);
    }
}
