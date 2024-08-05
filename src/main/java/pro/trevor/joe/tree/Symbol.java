package pro.trevor.joe.tree;

import pro.trevor.joe.tree.declaration.Declaration;

public class Symbol {
    private final Declaration declaration;
    private final String name;

    public Symbol(Declaration declaration, String name) {
        this.declaration = declaration;
        this.name = name;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public String getName() {
        return name;
    }
}
