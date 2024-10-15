package pro.trevor.joe.program.code.expression.literal;

import pro.trevor.joe.program.code.Expression;

public class BooleanExpression implements Expression {

    private final boolean value;

    public BooleanExpression(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
