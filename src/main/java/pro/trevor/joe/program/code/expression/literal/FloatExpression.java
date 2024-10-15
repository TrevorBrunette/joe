package pro.trevor.joe.program.code.expression.literal;

import pro.trevor.joe.program.code.Expression;

import java.math.BigDecimal;

public class FloatExpression implements Expression {

    private final BigDecimal value;

    public FloatExpression(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }
}
