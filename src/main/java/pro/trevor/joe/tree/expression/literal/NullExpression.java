package pro.trevor.joe.tree.expression.literal;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.IVisitor;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class NullExpression extends LiteralExpression {

    public NullExpression(Location location) {
        super(location);
    }

    @Override
    public Type type() {
        return new Type(ReturnType.NULL);
    }
}
