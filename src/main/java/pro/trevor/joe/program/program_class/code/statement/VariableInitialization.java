package pro.trevor.joe.program.program_class.code.statement;

import pro.trevor.joe.program.TypeReference;
import pro.trevor.joe.program.program_class.code.Expression;

public class VariableInitialization extends VariableDeclaration {

    private final Expression expression;

    public VariableInitialization(TypeReference type, String identifier, Expression expression) {
        super(type, identifier);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
