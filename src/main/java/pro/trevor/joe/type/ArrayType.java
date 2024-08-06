package pro.trevor.joe.type;

public class ArrayType extends Type {

    private final Type arrayType;

    public  ArrayType(Type arrayType) {
        super(ReturnType.ARRAY);
        assert arrayType != this;
        this.arrayType = arrayType;
    }

    public Type getArrayType() {
        return arrayType;
    }

    @Override
    public String toString() {
        return arrayType.toString() + "[]";
    }
}
