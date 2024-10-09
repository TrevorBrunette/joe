package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Symbol;
import pro.trevor.joe.parser.tree.Type;

public class VariableDeclaration extends AccessDeclaration implements ClassMember {

    private Type type;

    public VariableDeclaration(Location location, Symbol identifier, Access access, boolean isStatic, boolean isFinal, Type type) {
        super(location, identifier, access, isStatic, isFinal);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
