package pro.trevor.joe.program.code.expression.literal;

import pro.trevor.joe.program.code.Expression;

public class StringExpression implements Expression {

    private final String value;

    public StringExpression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
