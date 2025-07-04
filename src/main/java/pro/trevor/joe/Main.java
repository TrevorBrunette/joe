package pro.trevor.joe;

import pro.trevor.joe.lexer.Lexer;
import pro.trevor.joe.parser.ParseException;
import pro.trevor.joe.parser.Parser;
import pro.trevor.joe.parser.PrintVisitor;
import pro.trevor.joe.parser.tree.declaration.TopLevelDeclaration;
import pro.trevor.joe.program.AstToFileVisitor;
import pro.trevor.joe.program.File;
import pro.trevor.joe.program.llvm.Generator;

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
        AstToFileVisitor toFileVisitor = new AstToFileVisitor("text.joe");
        for (TopLevelDeclaration declaration : file) {
            PrintVisitor printVisitor = new PrintVisitor();
            printVisitor.visit(declaration);
            toFileVisitor.visit(declaration);
//            System.out.println(printVisitor);
        }

        File result = toFileVisitor.getFile();

        try (Generator generator = new Generator(result, new java.io.File("output.ll"))) {
            generator.generate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
