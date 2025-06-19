package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;

public abstract class AccessDeclaration extends Declaration {
    private final Access access;

    public AccessDeclaration(Location location, String identifier, Access access) {
        super(location, identifier);
        this.access = access;
    }

    public Access getAccess() {
        return access;
    }
}
