package pro.trevor.joe.program.code.expression.binary;

import pro.trevor.joe.program.code.Expression;

public class VariableAccessExpression extends BinaryOperatorExpression {
    public VariableAccessExpression(Expression leftOperand, Expression rightOperand) {
        super(leftOperand, rightOperand);
    }
}
