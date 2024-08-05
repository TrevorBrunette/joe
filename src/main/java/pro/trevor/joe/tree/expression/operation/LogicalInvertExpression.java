package pro.trevor.joe.tree.expression.operation;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ReturnType;

public class LogicalInvertExpression extends UnaryOperatorExpression {
    public LogicalInvertExpression(Location location, Expression operand) {
        super(location, operand);
    }
}
