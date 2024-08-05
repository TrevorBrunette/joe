package pro.trevor.joe.tree.expression.literal;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.IVisitor;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class CharExpression extends LiteralExpression {

    private final char value;

    public CharExpression(Location location, char value) {
        super(location);
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override
    public Type type() {
        return new Type(ReturnType.I32);
    }
}
