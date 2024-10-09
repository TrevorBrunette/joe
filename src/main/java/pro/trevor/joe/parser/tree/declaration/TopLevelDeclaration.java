package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Symbol;

public abstract class TopLevelDeclaration extends AccessDeclaration {
    public TopLevelDeclaration(Location location, Symbol identifier, Access access, boolean isStatic, boolean isFinal) {
        super(location, identifier, access, isStatic, isFinal);
    }
}
