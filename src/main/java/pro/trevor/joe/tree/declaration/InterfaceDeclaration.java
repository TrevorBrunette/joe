package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

import java.util.ArrayList;
import java.util.List;

public class InterfaceDeclaration extends TypeDeclaration  implements ClassMember, InterfaceMember {

    private final List<InterfaceMember> interfaceMembers;

    public InterfaceDeclaration(Location location, Symbol symbol, Access access, boolean isStatic, boolean isFinal) {
        super(location, symbol, access, isStatic, isFinal);
        this.interfaceMembers = new ArrayList<>();
    }

    public void addInterfaceMember(InterfaceMember interfaceMember) {
        interfaceMembers.add(interfaceMember);
    }

    public List<InterfaceMember> getInterfaceMembers() {
        return interfaceMembers;
    }
}
