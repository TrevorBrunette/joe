package pro.trevor.joe.tree.expression.operation;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class AdditionExpression extends BinaryOperatorExpression {
    public AdditionExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location, leftOperand, rightOperand, 11, Associativity.LEFT_TO_RIGHT);
    }

    @Override
    public Type type() {
        return new Type(ReturnType.highest(leftOperand.type().getReturnType(), rightOperand.type().getReturnType()));
    }
}
