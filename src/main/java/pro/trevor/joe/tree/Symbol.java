package pro.trevor.joe.tree;

public class Symbol {
    private final String name;

    public Symbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
