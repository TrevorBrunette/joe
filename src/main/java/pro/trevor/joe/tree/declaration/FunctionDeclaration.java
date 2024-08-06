package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.type.Type;
import pro.trevor.joe.tree.statement.Block;

import java.util.List;

public class FunctionDeclaration extends MemberDeclaration {

    private Symbol returnType;
    private final List<ParameterDeclaration> arguments;
    private Block code;

    public FunctionDeclaration(Location location, Symbol identifier, Declaration parent, boolean isStatic, boolean isFinal, Symbol returnType, List<ParameterDeclaration> arguments, Block code) {
        super(location, identifier, parent, isStatic, isFinal);
        this.returnType = returnType;
        this.arguments = arguments;
        this.code = code;
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

    public void setCode(Block code) {
        this.code = code;
    }

    public Block getCode() {
        return code;
    }
}
