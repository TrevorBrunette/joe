package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclaration extends TypeDeclaration implements ClassMember, EnumMember, InterfaceMember {

    private final List<ClassMember> classMembers;

    public ClassDeclaration(Location location, Symbol symbol, Access access, boolean isStatic, boolean isFinal) {
        super(location, symbol, access, isStatic, isFinal);
        this.classMembers = new ArrayList<>();
    }

    public void addMemberDeclaration(ClassMember classMember) {
        classMembers.add(classMember);
    }

    public List<ClassMember> getClassMembers() {
        return classMembers;
    }
}
