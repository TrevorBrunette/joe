package pro.trevor.joe.parser.tree;

import java.util.HashMap;

public class GlobalSymbolTable {
    private final HashMap<Symbol, SymbolTable> symbolTables;

    public GlobalSymbolTable() {
        symbolTables = new HashMap<>();
    }

    public SymbolTable getSymbolTable(Symbol symbol) {
        return symbolTables.get(symbol);
    }

    public void putSymbolTable(Symbol symbol, SymbolTable symbolTable) {
        symbolTables.put(symbol, symbolTable);
    }
}
