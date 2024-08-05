package pro.trevor.joe.tree.expression.literal;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.IVisitor;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class IntegerExpression extends LiteralExpression {

    private final long value;

    public IntegerExpression(Location location, long value) {
        super(location);
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public Type type() {
        ReturnType returnType;
        boolean positive = value >= 0;
        if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE) {
            returnType = positive ? ReturnType.U8 : ReturnType.I8;
        } else if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
            returnType = positive ? ReturnType.U16 : ReturnType.I16;
        } else if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
            returnType = positive ? ReturnType.U32 : ReturnType.I32;
        } else {
            returnType = positive ? ReturnType.U64 : ReturnType.I64;
        }
        return new Type(returnType);
    }
}
