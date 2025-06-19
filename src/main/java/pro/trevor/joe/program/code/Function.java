package pro.trevor.joe.program.code;

import pro.trevor.joe.parser.tree.declaration.Access;
import pro.trevor.joe.program.Parameter;
import pro.trevor.joe.program.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class Function {
    private final Access access;
    private final boolean isStatic;
    private final String identifier;
    private final List<Parameter> parameters;
    private final TypeReference returnType;
    private final Expressions.Block codeBlock;

    public Function(Access access, boolean isStatic, String identifier, List<Parameter> parameters, TypeReference returnType) {
        this.access = access;
        this.isStatic = isStatic;
        this.identifier = identifier;
        this.parameters = parameters;
        this.returnType = returnType;
        this.codeBlock = new Expressions.Block(new ArrayList<>());
    }

    public void addStatement(Statements.Statement statement) {
        codeBlock.statements().add(statement);
    }

    public Access getAccess() {
        return access;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public TypeReference getReturnType() {
        return returnType;
    }

    public Expressions.Block getCodeBlock() {
        return codeBlock;
    }
}
