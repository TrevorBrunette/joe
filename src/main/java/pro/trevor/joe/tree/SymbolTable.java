package pro.trevor.joe.tree;

import java.util.HashMap;
import java.util.Optional;

public class SymbolTable {

    private final HashMap<Symbol, SymbolInfo> symbolTable;

    public SymbolTable() {
        symbolTable = new HashMap<>();
    }

    public void put(Symbol symbol, SymbolInfo info) {
        symbolTable.put(symbol, info);
    }

    public Optional<SymbolInfo> get(Symbol symbol) {
        return Optional.ofNullable(symbolTable.get(symbol));
    }

    @Override
    public String toString() {
        return symbolTable.values().toString();
    }
}
