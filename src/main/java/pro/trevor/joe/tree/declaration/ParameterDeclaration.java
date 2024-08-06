package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

public class ParameterDeclaration extends Declaration {

    private final Symbol type;
    private final int number;

    public ParameterDeclaration(Location location, Symbol type, Symbol identifier, Declaration parent, int number) {
        super(location, identifier, parent);
        this.type = type;
        this.number = number;
    }

    public Symbol getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }
}
