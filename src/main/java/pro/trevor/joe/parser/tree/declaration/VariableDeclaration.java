package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Type;

public class VariableDeclaration extends AccessDeclaration implements ClassMember {

    private Type type;
    private final boolean isStatic;
    private final boolean isFinal;


    public VariableDeclaration(Location location, String identifier, Access access, boolean isStatic, boolean isFinal, Type type) {
        super(location, identifier, access);
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.type = type;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
