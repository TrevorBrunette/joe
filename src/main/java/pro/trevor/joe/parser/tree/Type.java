package pro.trevor.joe.parser.tree;

import pro.trevor.joe.lexer.TokenType;

public class Type {

    private final TokenType type;
    private final Symbol symbol;
    private final int arrayLevels;

    public Type(TokenType type) {
        assert type.isPrimitive();
        this.type = type;
        this.symbol = new Symbol(type.getText());
        this.arrayLevels = 0;
    }

    public Type(Symbol type) {
        this.type = TokenType.IDENTIFIER;
        this.symbol = type;
        this.arrayLevels = 0;
    }

    public Type(TokenType type, int arrayLevels) {
        assert type.isPrimitive() || type == TokenType.IDENTIFIER;
        this.type = type;
        this.symbol = new Symbol(type.getText());
        this.arrayLevels = arrayLevels;
    }

    public Type(Symbol type, int arrayLevels) {
        this.type = TokenType.IDENTIFIER;
        this.symbol = type;
        this.arrayLevels = arrayLevels;
    }

    public TokenType getType() {
        return type;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(symbol.getName());

        for (int i = 0; i < arrayLevels; ++i) {
            sb.append("[]");
        }

        return sb.toString();
    }
}
