package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.type.ClassType;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclaration extends MemberDeclaration {

    private final List<MemberDeclaration> memberDeclarations;

    public ClassDeclaration(Location location, Symbol symbol, ClassDeclaration classDeclaration, Access access, boolean isStatic, boolean isFinal) {
        super(location, new ClassType(symbol), symbol, classDeclaration, access, isStatic, isFinal);
        this.memberDeclarations = new ArrayList<>();
    }

    public ClassDeclaration(Location location, Symbol symbol, ClassDeclaration classDeclaration) {
        this(location, symbol, classDeclaration, Access.PRIVATE, false, false);
    }

    public ClassDeclaration(Location location, Symbol symbol) {
        this(location, symbol, null);
    }

    public void addMemberDeclaration(MemberDeclaration memberDeclaration) {
        memberDeclarations.add(memberDeclaration);
    }

    public List<MemberDeclaration> getMemberDeclarations() {
        return memberDeclarations;
    }
}
