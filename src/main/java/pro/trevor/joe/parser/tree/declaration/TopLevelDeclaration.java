package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;

public abstract class TopLevelDeclaration extends AccessDeclaration {
    public TopLevelDeclaration(Location location, String identifier, Access access, boolean isStatic, boolean isFinal) {
        super(location, identifier, access, isStatic, isFinal);
    }
}
