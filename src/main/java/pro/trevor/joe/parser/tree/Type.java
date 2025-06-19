package pro.trevor.joe.parser.tree;

import pro.trevor.joe.lexer.TokenType;

public class Type {

    private final TokenType type;
    private final String symbol;
    private final int arrayLevels;

    public Type(TokenType type) {
        assert type.isPrimitive();
        this.type = type;
        this.symbol = type.getText();
        this.arrayLevels = 0;
    }

    public Type(String type) {
        this.type = TokenType.IDENTIFIER;
        this.symbol = type;
        this.arrayLevels = 0;
    }

    public Type(TokenType type, int arrayLevels) {
        assert type.isPrimitive() || type == TokenType.IDENTIFIER;
        this.type = type;
        this.symbol = type.getText();
        this.arrayLevels = arrayLevels;
    }

    public Type(String type, int arrayLevels) {
        this.type = TokenType.IDENTIFIER;
        this.symbol = type;
        this.arrayLevels = arrayLevels;
    }


    public TokenType getType() {
        return type;
    }

    public int getArrayLevels() {
        return arrayLevels;
    }

    public String getString() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol + "[]".repeat(Math.max(0, arrayLevels));
    }
}
