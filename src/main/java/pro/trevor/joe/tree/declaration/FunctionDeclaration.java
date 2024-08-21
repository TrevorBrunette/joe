package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.Type;
import pro.trevor.joe.tree.statement.Block;

import java.util.List;

public class FunctionDeclaration extends TopLevelDeclaration implements ClassMember {

    private final Type returnType;
    private final List<ParameterDeclaration> arguments;
    private final Block code;

    public FunctionDeclaration(Location location, Symbol identifier, Access access, boolean isStatic, boolean isFinal, Type returnType, List<ParameterDeclaration> arguments, Block code) {
        super(location, identifier, access, isStatic, isFinal);
        this.returnType = returnType;
        this.arguments = arguments;
        this.code = code;
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
