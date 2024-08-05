package pro.trevor.joe.parser;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.lexer.Token;
import pro.trevor.joe.lexer.TokenType;

import java.util.Arrays;

public class ParseException extends Exception {

    private final Location location;

    public ParseException(Location location, String message) {
        super(location.toString() + ": " + message);
        this.location = location;
    }

    public ParseException(Location location, TokenType expected, Token actual) {
        super(String.format("Unexpected token `%s` at %s: expected token of type %s", actual.getText(), actual.getBeginLocation(), expected.name()));
        this.location = location;
    }

    public ParseException(Location location, TokenType[] expected, Token actual) {
        super(String.format("Unexpected token `%s` at %s: expected token of type %s", actual.getText(), actual.getBeginLocation(), Arrays.toString(expected)));
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
