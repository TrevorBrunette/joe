package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.Type;

import java.util.List;

public class EnumVariantDeclaration extends Declaration implements EnumMember {

    private final List<Type> types;

    public EnumVariantDeclaration(Location location, Symbol identifier, List<Type> types) {
        super(location, identifier);
        this.types = types;
    }

    public List<Type> getTypes() {
        return types;
    }
}
