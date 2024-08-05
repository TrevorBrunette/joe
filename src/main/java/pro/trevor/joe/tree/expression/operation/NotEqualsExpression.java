package pro.trevor.joe.tree.expression.operation;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class NotEqualsExpression extends BinaryOperatorExpression {
    public NotEqualsExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location, leftOperand, rightOperand);
    }

    @Override
    public Type type() {
        return new Type(ReturnType.BOOLEAN);
    }
}
