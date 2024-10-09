package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Symbol;
import pro.trevor.joe.parser.tree.Type;

import java.util.List;

public class FunctionStubDeclaration extends TopLevelDeclaration implements InterfaceMember {

    private final Type returnType;
    private final List<ParameterDeclaration> arguments;

    public FunctionStubDeclaration(Location location, Symbol identifier, Access access, boolean isStatic, boolean isFinal, Type returnType, List<ParameterDeclaration> arguments) {
        super(location, identifier, access, isStatic, isFinal);
        this.returnType = returnType;
        this.arguments = arguments;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<ParameterDeclaration> getArguments() {
        return arguments;
    }
}
