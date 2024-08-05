package pro.trevor.joe.lexer;

public class Token {

    private final TokenType type;
    private final String text;
    private final Location location;

    public Token(TokenType type, Location location) {
        this.type = type;
        this.text = type.getText();
        this.location = location;
    }

    public Token(TokenType type, String text, Location location) {
        this.type = type;
        this.text = text;
        this.location = location;
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public Location getEndLocation() {
        return location;
    }

    public Location getBeginLocation() {
        return new Location(location.line(), location.character() - text.length());
    }

    @Override
    public String toString() {
        return '\'' + text + '\'' + " @ " + location + " @ " + type.name();
    }
}
