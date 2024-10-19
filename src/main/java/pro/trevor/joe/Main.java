package pro.trevor.joe;

import pro.trevor.joe.lexer.Lexer;
import pro.trevor.joe.parser.ParseException;
import pro.trevor.joe.parser.Parser;
import pro.trevor.joe.parser.PrintVisitor;
import pro.trevor.joe.parser.tree.declaration.TopLevelDeclaration;
import pro.trevor.joe.program.Access;
import pro.trevor.joe.program.extern.ExternFunction;
import pro.trevor.joe.program.extern.ExternGenerationException;
import pro.trevor.joe.program.extern.ExternVariant;
import pro.trevor.joe.program.type.ArrayTypeReference;
import pro.trevor.joe.program.type.Primitive;
import pro.trevor.joe.program.type.PrimitiveTypeReference;
import pro.trevor.joe.program.type.TypeReference;

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
        TopLevelDeclaration tree = file.getFirst();
        PrintVisitor printVisitor = new PrintVisitor();
        printVisitor.visit(tree);
        System.out.println(printVisitor);
        System.out.println("extern fn printf(u8[]); translates to");
        System.out.println(new ExternFunction(Access.PUBLIC, ExternVariant.C, "printf", List.of(new ArrayTypeReference(new PrimitiveTypeReference(Primitive.U8))), TypeReference.UNIT_TYPE).toExternalLinkageString());
    }

}
