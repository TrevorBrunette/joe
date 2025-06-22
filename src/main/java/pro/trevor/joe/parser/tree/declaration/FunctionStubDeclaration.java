package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Type;
import pro.trevor.joe.program.type.TypeReference;

import java.util.List;

public class FunctionStubDeclaration extends TopLevelDeclaration implements InterfaceMember {

    private final boolean isStatic;
    private final boolean isFinal;
    private final boolean isExtern;
    private final Type returnType;
    private final List<ParameterDeclaration> arguments;
    private final boolean isVarArg;

    public FunctionStubDeclaration(Location location, String identifier, Access access, boolean isStatic, boolean isFinal, boolean isExtern, Type returnType, List<ParameterDeclaration> arguments) {
        super(location, identifier, access);
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.isExtern = isExtern;
        this.returnType = returnType;
        this.arguments = arguments;
        this.isVarArg = false;
    }

    public FunctionStubDeclaration(Location location, String identifier, Access access, boolean isStatic, boolean isFinal, boolean isExtern, Type returnType, List<ParameterDeclaration> arguments, boolean varArg) {
        super(location, identifier, access);
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.isExtern = isExtern;
        this.returnType = returnType;
        this.arguments = arguments;
        this.isVarArg = varArg;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isExtern() {
        return isExtern;
    }

    public boolean isVarArg() {
        return isVarArg;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<ParameterDeclaration> getArguments() {
        return arguments;
    }
}
