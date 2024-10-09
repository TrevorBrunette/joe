package pro.trevor.joe.parser.tree;

import pro.trevor.joe.lexer.Location;

public interface IStatement extends IVisitable {
    Location location();
}
