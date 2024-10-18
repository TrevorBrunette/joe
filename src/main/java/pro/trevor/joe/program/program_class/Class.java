package pro.trevor.joe.program.program_class;

import pro.trevor.joe.program.TopLevelNode;
import pro.trevor.joe.program.program_interface.Interface;
import pro.trevor.joe.program.type.NamedTypeReference;

import java.util.ArrayList;
import java.util.List;

public class Class extends TopLevelNode {

    public static final Class UNIVERSAL_PARENT = createUniversalParent();
    private static final NamedTypeReference UNIVERSAL_PARENT_TYPE_REFERENCE = new NamedTypeReference("joe::lang::Object");

    private final NamedTypeReference superclass;
    private final List<Interface> interfaces;
    private final List<MemberVariable> variables;
    private final List<MemberFunction> functions;

    public Class(List<TopLevelNode> innerTopLevelNodes) {
        super(innerTopLevelNodes);
        this.superclass = UNIVERSAL_PARENT_TYPE_REFERENCE;
        this.interfaces = new ArrayList<>();
        this.variables = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public Class(List<TopLevelNode> innerTopLevelNodes, NamedTypeReference parent) {
        super(innerTopLevelNodes);
        this.superclass = parent;
        this.interfaces = new ArrayList<>();
        this.variables = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void addInterface(Interface implementedInterface) {
        interfaces.add(implementedInterface);
    }

    public void addVariable(MemberVariable variable) {
        variables.add(variable);
    }

    public void addFunction(MemberFunction function) {
        functions.add(function);
    }

    public NamedTypeReference getSuperclass() {
        return superclass;
    }

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public List<MemberVariable> getVariables() {
        return variables;
    }

    public List<MemberFunction> getFunctions() {
        return functions;
    }

    private static Class createUniversalParent() {
        return new Class(List.of());
    }
}
