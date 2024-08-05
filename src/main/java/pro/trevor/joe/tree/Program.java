package pro.trevor.joe.tree;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.declaration.ClassDeclaration;

import java.util.ArrayList;
import java.util.List;

public class Program extends Node {

    private final List<ClassDeclaration> classes;

    public Program(Location location) {
        super(location);
        this.classes = new ArrayList<>();
    }

    public void addClass(ClassDeclaration classDeclaration) {
        classes.add(classDeclaration);
    }

    public List<ClassDeclaration> getClasses() {
        return classes;
    }
}
