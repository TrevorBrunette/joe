package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

import java.util.List;

public class FunctionStubDeclaration extends AccessDeclaration implements ClassMember, EnumMember, InterfaceMember {

    private Symbol returnType;
    private final List<ParameterDeclaration> arguments;

    public FunctionStubDeclaration(Location location, Symbol identifier, Declaration parent, Access access, boolean isStatic, boolean isFinal, Symbol returnType, List<ParameterDeclaration> arguments) {
        super(location, identifier, parent, access, isStatic, isFinal);
        this.returnType = returnType;
        this.arguments = arguments;
    }

    public Symbol getReturnType() {
        return returnType;
    }

    public void setReturnType(Symbol returnType) {
        this.returnType = returnType;
    }

    public List<ParameterDeclaration> getArguments() {
        return arguments;
    }
}
