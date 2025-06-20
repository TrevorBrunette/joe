package pro.trevor.joe.program.program_class;

import pro.trevor.joe.program.TopLevelType;
import pro.trevor.joe.program.code.ClassImplementation;
import pro.trevor.joe.program.code.InterfaceImplementation;
import pro.trevor.joe.program.type.NamedTypeReference;

import java.util.ArrayList;
import java.util.List;

public final class Class extends TopLevelType {

    private static final String UNIVERSAL_PARENT_CLASS_NAME = "Object";
    private static final NamedTypeReference UNIVERSAL_PARENT_TYPE_REFERENCE = new NamedTypeReference("joe::lang::" + UNIVERSAL_PARENT_CLASS_NAME);
    public static final Class UNIVERSAL_PARENT = createUniversalParent();

    private final NamedTypeReference superclass;
    private final List<MemberVariable> variables;
    private final ClassImplementation implementation;
    private final List<InterfaceImplementation> interfaceImplementations;

    public Class(NamedTypeReference name) {
        super(name);
        this.superclass = UNIVERSAL_PARENT_TYPE_REFERENCE;
        this.variables = new ArrayList<>();
        this.implementation = new ClassImplementation(name, new ArrayList<>());
        this.interfaceImplementations = new ArrayList<>();
    }

    public Class(NamedTypeReference name, NamedTypeReference parent) {
        super(name);
        this.superclass = parent;
        this.variables = new ArrayList<>();
        this.implementation = new ClassImplementation(name, new ArrayList<>());
        this.interfaceImplementations = new ArrayList<>();
    }

    // Universal parent type instantiation
    private Class() {
        super(UNIVERSAL_PARENT_TYPE_REFERENCE);
        this.superclass = null;
        this.variables = new ArrayList<>();;
        this.implementation = new ClassImplementation(super.getName(), new ArrayList<>());
        this.interfaceImplementations = new ArrayList<>();
    }

    public void addVariable(MemberVariable variable) {
        variables.add(variable);
    }

    public NamedTypeReference getSuperclass() {
        return superclass;
    }

    public List<MemberVariable> getVariables() {
        return variables;
    }

    public ClassImplementation getImplementation() {
        return implementation;
    }

    public void addInterfaceImplementation(InterfaceImplementation implementation) {
        interfaceImplementations.add(implementation);
    }

    public List<InterfaceImplementation> getInterfaceImplementations() {
        return interfaceImplementations;
    }

    private static Class createUniversalParent() {
        return new Class();
    }
}
