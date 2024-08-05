package pro.trevor.joe.tree;

import pro.trevor.joe.lexer.Location;

public interface IStatement extends IVisitable {
    Location location();
}
