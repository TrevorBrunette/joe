package pro.trevor.joe.lexer;

public class Util {

    public static boolean isNumeric(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isIdentifierBegin(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    public static boolean isIdentifierChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
    }

}
