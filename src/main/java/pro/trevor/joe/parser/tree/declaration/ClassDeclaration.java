package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.program.program_class.Class;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclaration extends TypeDeclaration {

    private final boolean isFinal;
    private final String superClassName;
    private final List<ClassMember> classMembers;

    public ClassDeclaration(Location location, String symbol, Access access, boolean isFinal, String superClass) {
        super(location, symbol, access);
        this.isFinal = isFinal;
        this.superClassName = superClass;
        this.classMembers = new ArrayList<>();
    }

    public ClassDeclaration(Location location, String symbol, Access access, boolean isFinal) {
        super(location, symbol, access);
        this.isFinal = isFinal;
        this.superClassName = Class.UNIVERSAL_PARENT.getName().name();
        this.classMembers = new ArrayList<>();
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

    public String getSuperClassName() {
        return superClassName;
    }
}
