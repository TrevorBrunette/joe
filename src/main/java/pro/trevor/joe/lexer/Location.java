package pro.trevor.joe.lexer;

public record Location(int line, int character) {
    @Override
    public String toString() {
        return line + ":" + character;
    }
}
