package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

public class VariableDeclaration extends MemberDeclaration {

    private Symbol type;

    public VariableDeclaration(Location location, Symbol identifier, ClassDeclaration classDeclaration, Access access, boolean isStatic, boolean isFinal, Symbol type) {
        super(location, identifier, classDeclaration, access, isStatic, isFinal);
        this.type = type;
    }

    public Symbol getType() {
        return type;
    }

    public void setType(Symbol type) {
        this.type = type;
    }
}
