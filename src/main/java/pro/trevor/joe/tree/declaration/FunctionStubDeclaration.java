package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;

import java.util.List;

public class FunctionStubDeclaration extends AccessDeclaration implements InterfaceMember {

    private final Symbol returnType;
    private final List<ParameterDeclaration> arguments;

    public FunctionStubDeclaration(Location location, Symbol identifier, Access access, boolean isStatic, boolean isFinal, Symbol returnType, List<ParameterDeclaration> arguments) {
        super(location, identifier, access, isStatic, isFinal);
        this.returnType = returnType;
        this.arguments = arguments;
    }

    public Symbol getReturnType() {
        return returnType;
    }

    public List<ParameterDeclaration> getArguments() {
        return arguments;
    }
}
