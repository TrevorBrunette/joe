package pro.trevor.joe.tree;

import pro.trevor.joe.lexer.Location;

public abstract class Node implements IVisitable {
    protected final Location location;

    public Node(Location location) {
        this.location = location;
    }

    public Location location() {
        return location;
    }
}
