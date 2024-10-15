package pro.trevor.joe.program.code.statement;

import pro.trevor.joe.program.TypeReference;
import pro.trevor.joe.program.code.Expression;

public class VariableInitializationStatement extends VariableDeclarationStatement {

    private final Expression expression;

    public VariableInitializationStatement(TypeReference type, String identifier, Expression expression) {
        super(type, identifier);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
