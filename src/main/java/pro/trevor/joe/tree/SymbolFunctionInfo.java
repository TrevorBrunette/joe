package pro.trevor.joe.tree;

public class SymbolFunctionInfo extends SymbolInfo {

    private final Type returnType;

    public SymbolFunctionInfo(Symbol symbol, Type returnType) {
        super(symbol, SymbolType.FUNCTION);
        this.returnType = returnType;
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return String.format("fn %s() %s", symbol, returnType);
    }
}
