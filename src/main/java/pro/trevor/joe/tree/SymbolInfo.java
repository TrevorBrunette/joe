package pro.trevor.joe.tree;

public class SymbolInfo {

    protected final Symbol symbol;
    protected final SymbolType type;

    public SymbolInfo(Symbol symbol, SymbolType type) {
        this.symbol = symbol;
        this.type = type;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public SymbolType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", symbol, type.name());
    }
}
