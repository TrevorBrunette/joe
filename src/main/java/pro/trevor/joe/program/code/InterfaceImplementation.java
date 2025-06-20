package pro.trevor.joe.program.code;

import pro.trevor.joe.program.type.NamedTypeReference;

import java.util.ArrayList;
import java.util.List;

public final class InterfaceImplementation {
    private final NamedTypeReference interfaceType;
    private final NamedTypeReference classType;
    private final List<Function> functions;

    public InterfaceImplementation(NamedTypeReference interfaceType, NamedTypeReference classType, List<Function> functions) {
        this.interfaceType = interfaceType;
        this.classType = classType;
        this.functions = functions;
    }

    public InterfaceImplementation(NamedTypeReference interfaceType, NamedTypeReference classType) {
        this.interfaceType = interfaceType;
        this.classType = classType;
        this.functions = new ArrayList<>();
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public NamedTypeReference getInterfaceType() {
        return interfaceType;
    }

    public NamedTypeReference getClassType() {
        return classType;
    }

    public List<Function> getFunctions() {
        return functions;
    }
}
