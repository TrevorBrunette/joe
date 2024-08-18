package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

import java.util.List;

public class EnumVariantDeclaration extends Declaration implements EnumMember {

    private final List<Symbol> types;

    public EnumVariantDeclaration(Location location, Symbol identifier, List<Symbol> types) {
        super(location, identifier);
        this.types = types;
    }

    public List<Symbol> getTypes() {
        return types;
    }
}
