package pro.trevor.joe.tree.expression.operation;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class LogicalXorExpression extends BinaryOperatorExpression {
    public LogicalXorExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location, leftOperand, rightOperand, 3, Associativity.LEFT_TO_RIGHT);
    }

    @Override
    public Type type() {
        return new Type(ReturnType.BOOLEAN);
    }
}
