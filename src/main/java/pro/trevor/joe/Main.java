package pro.trevor.joe;

import pro.trevor.joe.lexer.Lexer;
import pro.trevor.joe.parser.ParseException;
import pro.trevor.joe.parser.Parser;
import pro.trevor.joe.parser.PrintVisitor;
import pro.trevor.joe.parser.tree.declaration.TopLevelDeclaration;
import pro.trevor.joe.program.AstToFileVisitor;
import pro.trevor.joe.program.extern.ExternGenerationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParseException, ExternGenerationException {
        String text = "";
        try (FileInputStream fis = new FileInputStream("text.joe")) {
            text = new String(fis.readAllBytes());
        } catch (IOException ignored) {}
        Lexer lexer = new Lexer(text);
        Parser parser = new Parser(lexer);
        List<TopLevelDeclaration> file = parser.parseFile();
        AstToFileVisitor toFileVisitor = new AstToFileVisitor("text.joe");
        for (TopLevelDeclaration declaration : file) {
            PrintVisitor printVisitor = new PrintVisitor();
            printVisitor.visit(declaration);
            toFileVisitor.visit(declaration);
            System.out.println(printVisitor);
        }
        toFileVisitor.getFile().getTypes().forEach(System.out::println);
        toFileVisitor.getFile().getFunctions().forEach(System.out::println);
        toFileVisitor.getFile().getExternFunctions().forEach(System.out::println);
    }

}
