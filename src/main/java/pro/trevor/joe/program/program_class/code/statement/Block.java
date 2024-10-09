package pro.trevor.joe.program.program_class.code.statement;

import pro.trevor.joe.program.program_class.code.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Block implements Statement {

    private final List<Statement> statements;

    public Block() {
        statements = new ArrayList<>();
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    public void addStatements(Collection<Statement> statements) {
        this.statements.addAll(statements);
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
