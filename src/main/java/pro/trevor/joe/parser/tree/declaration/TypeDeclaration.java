package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;

public abstract class TypeDeclaration extends TopLevelDeclaration {
    public TypeDeclaration(Location location, String identifier, Access access, boolean isStatic, boolean isFinal) {
        super(location, identifier, access, isStatic, isFinal);
    }
}
