package pro.trevor.joe.program.code.expression;

import pro.trevor.joe.program.code.Expression;

import java.util.List;

public final class TupleExpression implements Expression {
    private final List<Expression> members;

    public TupleExpression(List<Expression> members) {
        this.members = members;
    }

    public TupleExpression(Expression... members) {
        this.members = List.of(members);
    }

    public List<Expression> members() {
        return members;
    }
}
