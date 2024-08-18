package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

public abstract class AccessDeclaration extends Declaration {
    private final Access access;
    private final boolean isStatic;
    private final boolean isFinal;

    public AccessDeclaration(Location location, Symbol identifier, Access access, boolean isStatic, boolean isFinal) {
        super(location, identifier);
        this.access = access;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
    }

    public Access getAccess() {
        return access;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
