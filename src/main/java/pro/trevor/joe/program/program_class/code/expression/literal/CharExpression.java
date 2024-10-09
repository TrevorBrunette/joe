package pro.trevor.joe.program.program_class.code.expression.literal;

import pro.trevor.joe.program.program_class.code.Expression;

public class CharExpression implements Expression {

    private final char value;

    public CharExpression(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }
}
