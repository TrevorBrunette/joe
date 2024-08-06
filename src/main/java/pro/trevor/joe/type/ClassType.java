package pro.trevor.joe.type;

import pro.trevor.joe.tree.Symbol;

public class ClassType extends Type {

    private final Symbol classIdentifier;

    public ClassType(Symbol classIdentifier) {
        super(ReturnType.OBJECT);
        this.classIdentifier = classIdentifier;
    }

    public Symbol getClassIdentifier() {
        return classIdentifier;
    }

    @Override
    public String toString() {
        return classIdentifier.getName();
    }
}
