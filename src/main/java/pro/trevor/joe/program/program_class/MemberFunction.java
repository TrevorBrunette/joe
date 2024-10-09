package pro.trevor.joe.program.program_class;

import pro.trevor.joe.program.Access;
import pro.trevor.joe.program.Parameter;
import pro.trevor.joe.program.TypeReference;
import pro.trevor.joe.program.program_class.code.Statement;
import pro.trevor.joe.program.program_class.code.statement.Block;

import java.util.List;

public class MemberFunction {
    private final Access access;
    private final boolean isStatic;
    private final String identifier;
    private final List<Parameter> parameters;
    private final TypeReference returnType;
    private final Block codeBlock;

    public MemberFunction(Access access, boolean isStatic, String identifier, List<Parameter> parameters, TypeReference returnType) {
        this.access = access;
        this.isStatic = isStatic;
        this.identifier = identifier;
        this.parameters = parameters;
        this.returnType = returnType;
        this.codeBlock = new Block();
    }

    public void addStatement(Statement statement) {
        codeBlock.addStatement(statement);
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

    public Block getCodeBlock() {
        return codeBlock;
    }
}
