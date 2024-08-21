package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.Type;

public class ParameterDeclaration extends Declaration {

    private final Type type;

    public ParameterDeclaration(Location location, Type type, Symbol identifier) {
        super(location, identifier);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
