package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Symbol;

import java.util.ArrayList;
import java.util.List;

public class EnumDeclaration extends TypeDeclaration implements ClassMember, EnumMember, InterfaceMember{

    private final List<EnumMember> enumMembers;

    public EnumDeclaration(Location location, Symbol identifier, Access access, boolean isStatic, boolean isFinal) {
        super(location, identifier, access, isStatic, isFinal);
        this.enumMembers = new ArrayList<>();
    }

    public void addEnumMember(EnumMember enumMember) {
        enumMembers.add(enumMember);
    }

    public List<EnumMember> getEnumMembers() {
        return enumMembers;
    }
}
