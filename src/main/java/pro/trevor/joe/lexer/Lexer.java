package pro.trevor.joe.lexer;

import java.util.Iterator;
import java.util.Optional;

public class Lexer {

    private static final char INVALID = (char) -1;

    private final Iterator<Character> text;
    private int previousLine;
    private int previousCharacter;
    private int line;
    private int character;
    private char c;

    public Lexer(String text) {
        this.text = text.chars().mapToObj((i) -> (char) i).iterator();
        this.previousLine = 1;
        this.previousCharacter = 1;
        this.line = 1;
        this.character = 1;
        this.c = this.text.hasNext() ? this.text.next() : INVALID;
    }

    public Token getNextToken() {
        Token token = null;
        while (token == null || token.getType() == TokenType.COMMENT) {
            token = nextToken();
        }
        return token;
    }

    private Token token(TokenType type) {
        return new Token(type, new Location(previousLine, previousCharacter));
    }

    private Token token(TokenType type, String text) {
        return new Token(type, text, new Location(previousLine, previousCharacter));
    }

    private Token nextToken() {
        skipWhitespace();
        switch (c) {
            case INVALID -> {
                return token(TokenType.EOF);
            }
            case '\'' -> {
                return nextCharacterLiteralToken();
            }
            case '"' -> {
                return nextStringLiteralToken();
            }
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                return nextNumericLiteralToken();
            }
            default -> {
                if (Util.isIdentifierBegin(c)) {
                    return nextIdentifierToken();
                } else {
                    return nextOperatorToken();
                }
            }
        }
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(c)) {
            nextChar();
        }
    }

    private Token nextIdentifierToken() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(c);
        } while (Util.isIdentifierChar(nextChar()));
        String identifier = sb.toString();
        Optional<TokenType> keyword = TokenType.getKeyword(identifier);
        if (keyword.isPresent()) {
            return token(keyword.get());
        } else {
            Optional<TokenType> primitive = TokenType.getPrimitive(identifier);
            return primitive.map(this::token).orElseGet(() -> token(TokenType.IDENTIFIER, identifier));
        }
    }

    private Token nextCharacterLiteralToken() {
        StringBuilder sb = new StringBuilder("'");
        boolean escaped = false;
        while (nextChar() != '\'' || escaped) {
            sb.append(c);
            if (c == '\'') {
                escaped = !escaped;
            }
        }
        sb.append(c);
        nextChar();
        return token(TokenType.CHAR_IMMEDIATE, sb.toString());
    }

    private Token nextStringLiteralToken() {
        StringBuilder sb = new StringBuilder("\"");
        boolean escaped = false;
        while (nextChar() != '"' || escaped) {
            sb.append(c);
            if (c == '\'') {
                escaped = !escaped;
            }
        }
        sb.append(c);
        nextChar();
        return token(TokenType.STRING_IMMEDIATE, sb.toString());
    }

    private Token nextNumericLiteralToken() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(c);
        } while (Util.isNumeric(nextChar()));
        if (c == '.') {
            sb.append(c);
            while (Util.isNumeric(nextChar())) {
                sb.append(c);
            }
            return token(TokenType.FLOAT_IMMEDIATE, sb.toString());
        } else {
            return token(TokenType.INTEGER_IMMEDIATE, sb.toString());
        }
    }

    private Token nextOperatorToken() {
        TokenType t;
        skipWhitespace();
        switch (c) {
            case '<' -> {
                switch (nextChar()) {
                    case '<' -> {
                        t = TokenType.SHIFT_LEFT;
                        nextChar();
                    }
                    case '=' -> {
                        t = TokenType.LESS_EQUAL;
                        nextChar();
                    }
                    default -> {
                        t = TokenType.LESS_THAN;
                    }
                }
            }
            case '>' -> {
                switch (nextChar()) {
                    case '>' -> {
                        switch (nextChar()) {
                            case '>' -> {
                                t = TokenType.SHIFT_RIGHT_LOGICAL;
                                nextChar();
                            }
                            default -> t = TokenType.SHIFT_RIGHT;
                        }
                    }
                    case '=' -> {
                        t = TokenType.GREATER_EQUAL;
                        nextChar();
                    }
                    default -> t = TokenType.GREATER_THAN;
                }
            }
            case '~' -> {
                switch (nextChar()) {
                    case '=' -> {
                        t = TokenType.NOT_ASSIGN;
                        nextChar();
                    }
                    default -> t = TokenType.BNOT;
                }
            }
            case '!' -> {
                switch (nextChar()) {
                    case '=' -> {
                        t = TokenType.NOT_EQUALS;
                        nextChar();
                    }
                    default -> t = TokenType.LNOT;
                }
            }
            case '%' -> {
                t = TokenType.MOD;
                nextChar();
            }
            case '^' -> {
                switch (nextChar()) {
                    case '=' -> {
                        t = TokenType.XOR_ASSIGN;
                        nextChar();
                    }
                    default -> t = TokenType.XOR;
                }
            }
            case '&' -> {
                switch (nextChar()) {
                    case '&' -> {
                        t = TokenType.LAND;
                        nextChar();
                    }
                    case '='-> {
                        t = TokenType.AND_ASSIGN;
                        nextChar();
                    }
                    default -> t = TokenType.BAND;
                }
            }
            case '|' -> {
                switch (nextChar()) {
                    case '|' -> {
                        t = TokenType.LOR;
                        nextChar();
                    }
                    case '=' -> {
                        t = TokenType.OR_ASSIGN;
                        nextChar();
                    }
                    default -> t = TokenType.BOR;
                }
            }
            case '+' -> {
                switch (nextChar()) {
                    case '=' -> {
                        t = TokenType.ADD_ASSIGN;
                        nextChar();
                    }
                    default ->t = TokenType.ADD;
                }
            }
            case '-' -> {
                switch (nextChar()) {
                    case '=' -> {
                        t = TokenType.SUB_ASSIGN;
                        nextChar();
                    }
                    default -> t = TokenType.SUB;
                }
            }
            case '*' -> {
                switch (nextChar()) {
                    case '=' -> {
                        t = TokenType.MUL_ASSIGN;
                        nextChar();
                    }
                    default -> t = TokenType.MUL;
                }
            }
            case '/' -> {
                switch (nextChar()) {
                    case '/' -> {
                        continueToNewline();
                        t = TokenType.COMMENT;
                    }
                    case '*'-> {
                        continueToEndComment();
                        t = TokenType.COMMENT;
                    }
                    case '=' -> {
                        t = TokenType.DIV_ASSIGN;
                        nextChar();
                    }
                    default -> {
                        t = TokenType.DIV;
                        nextChar();
                    }
                }
            }
            case '=' -> {
                switch (nextChar()) {
                    case '=' -> {
                        t = TokenType.EQUALS;
                        nextChar();
                    }
                    default -> t = TokenType.ASSIGN;
                }
            }
            case '(' -> {
                t = TokenType.LPAREN;
                nextChar();
            }
            case ')' -> {
                t = TokenType.RPAREN;
                nextChar();
            }
            case '[' -> {
                t = TokenType.LBRACKET;
                nextChar();
            }
            case ']' -> {
                t = TokenType.RBRACKET;
                nextChar();
            }
            case '{' -> {
                t = TokenType.LBRACE;
                nextChar();
            }
            case '}' -> {
                t = TokenType.RBRACE;
                nextChar();
            }
            case '.' -> {
                t = TokenType.PERIOD;
                nextChar();
            }
            case ',' -> {
                t = TokenType.COMMA;
                nextChar();
            }
            case '?' -> {
                t = TokenType.QUESTION;
                nextChar();
            }
            case ':' -> {
                t = TokenType.COLON;
                nextChar();
            }
            case ';' -> {
                t = TokenType.SEMICOLON;
                nextChar();
            }
            default -> {
                t = TokenType.ERROR;
                nextChar();
            }
        }
        return token(t);
    }

    private void continueToChar(char c) {
        while (nextChar() != c && c != INVALID) {
        }
    }

    private void continueToNewline() {
        continueToChar('\n');
        nextChar();
    }

    private void continueToEndComment() {
        do {
            continueToChar('*');
            nextChar();
        } while (c != '/' && c != INVALID);
        nextChar();
    }

    private char nextChar() {
        if (!text.hasNext()) {
            c = INVALID;
        } else {
            c = text.next();
            if (c == '\n') {
                previousLine = line;
                previousCharacter = character;
                ++line;
                character = 1;
            } else {
                previousCharacter = character;
                ++character;
            }
        }
        return c;
    }

}
