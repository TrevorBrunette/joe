package pro.trevor.joe.tree.declaration;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.type.Type;
import pro.trevor.joe.tree.statement.Block;

import java.util.List;

public class FunctionDeclaration extends MemberDeclaration {

    private final List<ParameterDeclaration> arguments;
    private Block code;

    public FunctionDeclaration(Location location, Symbol identifier, Declaration parent, boolean isStatic, boolean isFinal, List<ParameterDeclaration> arguments, Type returnType, Block code) {
        super(location, returnType, identifier, parent, isStatic, isFinal);
        this.arguments = arguments;
        this.code = code;
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
