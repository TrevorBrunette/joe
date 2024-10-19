package pro.trevor.joe.program.code;

import pro.trevor.joe.program.type.NamedTypeReference;

import java.util.ArrayList;
import java.util.List;

public final class InterfaceImplementation {
    private final NamedTypeReference type;
    private final List<Function> functions;

    public InterfaceImplementation(NamedTypeReference type, List<Function> functions) {
        this.type = type;
        this.functions = functions;
    }

    public InterfaceImplementation(NamedTypeReference type) {
        this.type = type;
        this.functions = new ArrayList<>();
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public NamedTypeReference getType() {
        return type;
    }

    public List<Function> getFunctions() {
        return functions;
    }
}
