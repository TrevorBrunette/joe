package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

public class VariableDeclaration extends AccessDeclaration implements ClassMember {

    private Symbol type;

    public VariableDeclaration(Location location, Symbol identifier, Access access, boolean isStatic, boolean isFinal, Symbol type) {
        super(location, identifier, access, isStatic, isFinal);
        this.type = type;
    }

    public Symbol getType() {
        return type;
    }

    public void setType(Symbol type) {
        this.type = type;
    }
}
