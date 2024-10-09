package pro.trevor.joe.program.program_class.code.expression.literal;

import pro.trevor.joe.program.program_class.code.Expression;

import java.math.BigInteger;

public class IntegerExpression implements Expression {

    private final BigInteger value;

    public IntegerExpression(BigInteger value) {
        this.value = value;
    }

    public BigInteger getValue() {
        return value;
    }
}
