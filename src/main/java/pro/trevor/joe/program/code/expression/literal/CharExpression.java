package pro.trevor.joe.program.code.expression.literal;

import pro.trevor.joe.program.code.Expression;

public class CharExpression implements Expression {

    private final char value;

    public CharExpression(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }
}
