package pro.trevor.joe.program.program_interface;

import pro.trevor.joe.program.TopLevelType;
import pro.trevor.joe.program.type.NamedTypeReference;

import java.util.ArrayList;
import java.util.List;

public final class Interface extends TopLevelType {

    private final List<FunctionDeclaration> functions;

    public Interface(NamedTypeReference name) {
        super(name);
        this.functions = new ArrayList<>();
    }

    public List<FunctionDeclaration> getFunctionDeclarations() {
        return functions;
    }
}
