package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.type.Type;

public class VariableDeclaration extends MemberDeclaration {

    public VariableDeclaration(Location location, Type type, Symbol identifier, ClassDeclaration classDeclaration, Access access, boolean isStatic, boolean isFinal) {
        super(location, type, identifier, classDeclaration, access, isStatic, isFinal);
    }
}
