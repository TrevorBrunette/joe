package pro.trevor.joe.tree.expression.literal;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.IVisitor;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class FloatExpression extends LiteralExpression {

    private final float value;

    public FloatExpression(Location location, float value) {
        super(location);
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public Type type() {
        ReturnType returnType;
        if (value <= Float.MAX_VALUE && value >= Float.MIN_VALUE) {
            returnType = ReturnType.F32;
        } else {
            returnType = ReturnType.F64;
        }
        return new Type(returnType);
    }
}
