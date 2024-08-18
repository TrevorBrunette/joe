package pro.trevor.joe.tree;

public class SymbolFunctionInfo extends SymbolInfo {

    private final Symbol returnType;

    public SymbolFunctionInfo(Symbol symbol, Symbol returnType) {
        super(symbol, SymbolType.FUNCTION);
        this.returnType = returnType;
    }

    public Symbol getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return String.format("fn %s(%s)", symbol, returnType);
    }
}
