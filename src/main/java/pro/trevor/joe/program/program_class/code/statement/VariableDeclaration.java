package pro.trevor.joe.program.program_class.code.statement;

import pro.trevor.joe.program.TypeReference;
import pro.trevor.joe.program.program_class.code.Statement;

public class VariableDeclaration implements Statement {

    private final TypeReference type;
    private final String identifier;

    public VariableDeclaration(TypeReference type, String identifier) {
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
