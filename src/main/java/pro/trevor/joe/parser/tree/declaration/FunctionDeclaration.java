package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.parser.tree.Type;
import pro.trevor.joe.parser.tree.statement.Block;

import java.util.List;

public class FunctionDeclaration extends TopLevelDeclaration implements ClassMember {

    private final boolean isStatic;
    private final boolean isFinal;
    private final Type returnType;
    private final List<ParameterDeclaration> arguments;
    private final Block code;

    public FunctionDeclaration(Location location, String identifier, Access access, boolean isStatic, boolean isFinal, Type returnType, List<ParameterDeclaration> arguments, Block code) {
        super(location, identifier, access);
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.returnType = returnType;
        this.arguments = arguments;
        this.code = code;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<ParameterDeclaration> getArguments() {
        return arguments;
    }

    public Block getCode() {
        return code;
    }
}
