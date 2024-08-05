package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.type.Type;

public abstract class MemberDeclaration extends Declaration {

    private final Access access;
    private final boolean isStatic;
    private final boolean isFinal;

    public MemberDeclaration(Location location, Type type, Symbol identifier, Declaration parent, Access access, boolean isStatic, boolean isFinal) {
        super(location, type, identifier, parent);
        this.access = access;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
    }

    public MemberDeclaration(Location location, Type type, Symbol identifier, Declaration parent, boolean isStatic, boolean isFinal) {
        this(location, type, identifier, parent, Access.PRIVATE, isStatic, isFinal);
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
