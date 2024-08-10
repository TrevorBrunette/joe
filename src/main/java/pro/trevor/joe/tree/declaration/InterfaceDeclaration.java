package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

import java.util.ArrayList;
import java.util.List;

public class InterfaceDeclaration extends AccessDeclaration {

    private final List<InterfaceMember> interfaceMembers;

    public InterfaceDeclaration(Location location, Symbol symbol, ClassDeclaration classDeclaration, Access access, boolean isStatic, boolean isFinal) {
        super(location, symbol, classDeclaration, access, isStatic, isFinal);
        this.interfaceMembers = new ArrayList<>();
    }

    public InterfaceDeclaration(Location location, Symbol symbol, ClassDeclaration classDeclaration) {
        this(location, symbol, classDeclaration, Access.PRIVATE, false, false);
    }

    public InterfaceDeclaration(Location location, Symbol symbol) {
        this(location, symbol, null);
    }

    public void addInterfaceMember(InterfaceMember interfaceMember) {
        interfaceMembers.add(interfaceMember);
    }

    public List<InterfaceMember> getInterfaceMembers() {
        return interfaceMembers;
    }
}
