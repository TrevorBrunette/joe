package pro.trevor.joe.tree;

public class SymbolVariableInfo extends SymbolInfo {

    private final Symbol declaredType;

    public SymbolVariableInfo(Symbol symbol, Symbol declaredType) {
        super(symbol, SymbolType.VARIABLE);
        this.declaredType = declaredType;
    }

    public Symbol getDeclaredType() {
        return declaredType;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", symbol, declaredType);
    }
}
