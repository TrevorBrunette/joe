package pro.trevor.joe.program.code.expression.binary;

import pro.trevor.joe.program.code.Expression;

public class AssignmentExpression extends BinaryOperatorExpression {
    public AssignmentExpression(Expression leftOperand, Expression rightOperand) {
        super(leftOperand, rightOperand);
    }
}
