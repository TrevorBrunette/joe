package pro.trevor.joe.tree.expression.type;

import pro.trevor.joe.tree.IVisitable;

public class Type implements IVisitable {

    private final ReturnType returnType;

    public Type(ReturnType returnType) {
        this.returnType = returnType;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return returnType.name().toLowerCase();
    }
}
