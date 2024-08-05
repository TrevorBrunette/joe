package pro.trevor.joe;

import pro.trevor.joe.lexer.Lexer;
import pro.trevor.joe.parser.ParseException;
import pro.trevor.joe.parser.Parser;
import pro.trevor.joe.parser.PrintVisitor;
import pro.trevor.joe.tree.declaration.ClassDeclaration;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws ParseException {
        String text = "";
        try (FileInputStream fis = new FileInputStream("text.txt")) {
            text = new String(fis.readAllBytes());
        } catch (IOException ignored) {}
        Lexer lexer = new Lexer(text);
        Parser parser = new Parser(lexer);
        ClassDeclaration tree = parser.parseClass(null);
        PrintVisitor visitor = new PrintVisitor();
        visitor.visit(tree);
        System.out.println(visitor);
    }

}
