package pro.trevor.joe.tree.expression;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.expression.type.ArrayType;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class ArrayIndexExpression extends Expression {

    private final Expression array;
    private final Expression index;

    public ArrayIndexExpression(Location location, Expression array, Expression index) {
        super(location);
        assert array.type().getReturnType() == ReturnType.ARRAY;
        assert index.type().getReturnType().isUnsigned();
        this.array = array;
        this.index = index;
    }

    public Expression getArray() {
        return array;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public Type type() {
        return ((ArrayType) array.type()).getArrayType();
    }

    @Override
    public boolean isAssignable() {
        return true;
    }
}
