package pro.trevor.joe.tree.expression.operation;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class AssignmentExpression extends BinaryOperatorExpression {
    public AssignmentExpression(Location location, Expression leftOperand, Expression rightOperand) {
        super(location, leftOperand, rightOperand, 1, Associativity.RIGHT_TO_LEFT);
    }

    @Override
    public Type type() {
        return rightOperand.type();
    }
}
