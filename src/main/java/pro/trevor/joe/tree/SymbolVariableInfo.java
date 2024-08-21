package pro.trevor.joe.tree;

public class SymbolVariableInfo extends SymbolInfo {

    private final Type declaredType;

    public SymbolVariableInfo(Symbol symbol, Type declaredType) {
        super(symbol, SymbolType.VARIABLE);
        this.declaredType = declaredType;
    }

    public Type getDeclaredType() {
        return declaredType;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", symbol, declaredType);
    }
}
