package pro.trevor.joe.program;

import pro.trevor.joe.program.code.ClassImplementation;
import pro.trevor.joe.program.code.Function;
import pro.trevor.joe.program.code.InterfaceImplementation;
import pro.trevor.joe.program.extern.ExternFunction;
import pro.trevor.joe.program.program_class.Class;
import pro.trevor.joe.program.type.NamedTypeReference;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgramTable {

    private final HashMap<Path, File> pathToFile;
    private final HashMap<NamedTypeReference, TopLevelType> types;
    private final HashMap<Path, Function> functions;
    private final HashMap<Path, ExternFunction> externFunctions;

    public ProgramTable() {
        pathToFile = new HashMap<>();
        types = new HashMap<>();
        functions = new HashMap<>();
        externFunctions = new HashMap<>();
    }

    public void emplaceImplementations() {
        for (File file : pathToFile.values()) {
            for (ClassImplementation classImplementation : file.getClassImplementations()) {
                TopLevelType type = this.types.get(classImplementation.type());
                if (type == null) {
                    throw new Error("Missing type " + classImplementation.type().toString());
                }
                if (type instanceof Class clazz) {
                    clazz.getImplementation().functions().addAll(classImplementation.functions());
                } else {
                    throw new Error("Implementation block defined for non-class type: " + classImplementation.type().toString());
                }
            }

            for (InterfaceImplementation interfaceImplementation : file.getInterfaceImplementations()) {
                TopLevelType type = this.types.get(interfaceImplementation.getInterfaceType());
                if (type == null) {
                    throw new Error("Missing type " + interfaceImplementation.getInterfaceType().toString());
                }
                if (type instanceof Class clazz) {
                    clazz.addInterfaceImplementation(interfaceImplementation);
                } else {
                    throw new Error("Implementation block defined for non-class type: " + interfaceImplementation.getInterfaceType().toString());
                }
            }
        }
    }

    public void put(Path path, File file) {
        pathToFile.put(path, file);

        Path pathIncludingFile = new Path(new ArrayList<>(path.elements()));
        pathIncludingFile.elements().add(file.getName());

        for (TopLevelType type : file.getTypes()) {
            this.types.put(type.getName(), type);
        }

        for (Function function : file.getFunctions()) {
            this.functions.put(path, function);
        }

        for (ExternFunction externFunction : file.getExternFunctions()) {
            this.externFunctions.put(path, externFunction);
        }
    }

    public File getFile(Path path) {
        return pathToFile.get(path);
    }

    public TopLevelType getType(NamedTypeReference type) {
        return types.get(type);
    }

    public Function getFunction(Path path) {
        return functions.get(path);
    }

    public ExternFunction getExternFunction(Path path) {
        return externFunctions.get(path);
    }

}
