package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;

import java.util.ArrayList;
import java.util.List;

public class EnumDeclaration extends TypeDeclaration {

    private final List<EnumMember> enumMembers;

    public EnumDeclaration(Location location, String identifier, Access access) {
        super(location, identifier, access);
        this.enumMembers = new ArrayList<>();
    }

    public void addEnumMember(EnumMember enumMember) {
        enumMembers.add(enumMember);
    }

    public List<EnumMember> getEnumMembers() {
        return enumMembers;
    }
}
