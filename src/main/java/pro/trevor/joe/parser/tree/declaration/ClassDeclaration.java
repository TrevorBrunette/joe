package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclaration extends TypeDeclaration {

    private final List<ClassMember> classMembers;
    private final boolean isFinal;

    public ClassDeclaration(Location location, String symbol, Access access, boolean isFinal) {
        super(location, symbol, access);
        this.classMembers = new ArrayList<>();
        this.isFinal = isFinal;
    }

    public void addMemberDeclaration(ClassMember classMember) {
        classMembers.add(classMember);
    }

    public List<ClassMember> getClassMembers() {
        return classMembers;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
