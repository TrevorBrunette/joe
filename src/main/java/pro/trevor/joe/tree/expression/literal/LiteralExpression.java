package pro.trevor.joe.tree.expression.literal;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.Expression;

public abstract class LiteralExpression extends Expression {
    public LiteralExpression(Location location) {
        super(location);
    }

    @Override
    public boolean isAssignable() {
        return false;
    }
}
