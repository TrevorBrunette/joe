package pro.trevor.joe;

import pro.trevor.joe.lexer.Lexer;
import pro.trevor.joe.lexer.Token;
import pro.trevor.joe.lexer.TokenType;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String text = "";
        try (FileInputStream fis = new FileInputStream("text.txt")) {
            text = new String(fis.readAllBytes());
        } catch (IOException ignored) {}
        Lexer lexer = new Lexer(text);
        Token token;
        do {
            token = lexer.getNextToken();
            System.out.println(token);
        }
        while (token.getType() != TokenType.ERROR && token.getType() != TokenType.EOF);
    }

}
