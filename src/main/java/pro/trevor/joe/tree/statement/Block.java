package pro.trevor.joe.tree.statement;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.Node;
import pro.trevor.joe.tree.IStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Block extends Node implements IStatement {

    private final List<IStatement> statements;

    public Block(Location location) {
        super(location);
        statements = new ArrayList<>();
    }

    public void addStatement(IStatement statement) {
        statements.add(statement);
    }

    public void addStatements(Collection<IStatement> statements) {
        this.statements.addAll(statements);
    }

    public List<IStatement> getStatements() {
        return statements;
    }
}
