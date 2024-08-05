package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.type.Type;

public class ParameterDeclaration extends Declaration {

    private final int number;

    public ParameterDeclaration(Location location, Type type, Symbol identifier, Declaration parent, int number) {
        super(location, type, identifier, parent);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
