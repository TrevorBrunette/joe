package pro.trevor.joe.program;

import pro.trevor.joe.program.program_class.Class;
import pro.trevor.joe.program.type.NamedTypeReference;

import java.util.HashMap;

public class ProgramTable {

    private final HashMap<NamedTypeReference, Class> pathToClass;
    private final HashMap<Class, NamedTypeReference> classToPath;

    public ProgramTable() {
        pathToClass = new HashMap<>();
        classToPath = new HashMap<>();
    }

    public void put(NamedTypeReference type, Class clazz) {
        pathToClass.put(type, clazz);
        classToPath.put(clazz, type);
    }

    public Class getClass(NamedTypeReference type) {
        return pathToClass.get(type);
    }

    public NamedTypeReference getPath(Class clazz) {
        return classToPath.get(clazz);
    }

}
