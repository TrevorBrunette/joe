package pro.trevor.joe.program.program_class.code;

import java.util.ArrayList;
import java.util.List;

public class CodeBlock {
    private final List<Statement> statements;

    public CodeBlock() {
        this.statements = new ArrayList<>();
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
