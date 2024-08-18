package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

public class ParameterDeclaration extends Declaration {

    private final Symbol type;

    public ParameterDeclaration(Location location, Symbol type, Symbol identifier) {
        super(location, identifier);
        this.type = type;
    }

    public Symbol getType() {
        return type;
    }
}
