package pro.trevor.joe;

import pro.trevor.joe.lexer.Lexer;
import pro.trevor.joe.parser.ParseException;
import pro.trevor.joe.parser.Parser;
import pro.trevor.joe.parser.PrintVisitor;
import pro.trevor.joe.parser.SymbolVisitor;
import pro.trevor.joe.tree.declaration.TopLevelDeclaration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParseException {
        String text = "";
        try (FileInputStream fis = new FileInputStream("text.joe")) {
            text = new String(fis.readAllBytes());
        } catch (IOException ignored) {}
        Lexer lexer = new Lexer(text);
        Parser parser = new Parser(lexer);
        List<TopLevelDeclaration> file = parser.parseFile();
        TopLevelDeclaration tree = file.getFirst();
        PrintVisitor printVisitor = new PrintVisitor();
        printVisitor.visit(tree);
        System.out.println(printVisitor);
        SymbolVisitor symbolVisitor = new SymbolVisitor();
        symbolVisitor.visit(tree);
        System.out.println(symbolVisitor.getSymbolTable().values());
    }

}
