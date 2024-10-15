package pro.trevor.joe.program.code.statement;

import pro.trevor.joe.program.code.Statement;
import pro.trevor.joe.program.type.TypeReference;

public class VariableDeclarationStatement implements Statement {

    private final TypeReference type;
    private final String identifier;

    public VariableDeclarationStatement(TypeReference type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public TypeReference getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }
}
