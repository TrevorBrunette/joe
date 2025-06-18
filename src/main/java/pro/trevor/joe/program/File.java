package pro.trevor.joe.program;

import pro.trevor.joe.program.code.ClassImplementation;
import pro.trevor.joe.program.code.Function;
import pro.trevor.joe.program.code.InterfaceImplementation;
import pro.trevor.joe.program.extern.ExternFunction;

import java.util.ArrayList;
import java.util.List;

public class File {

    private final String name;
    private final List<TopLevelType> types;
    private final List<ClassImplementation> classImplementations;
    private final List<InterfaceImplementation> interfaceImplementations;
    private final List<Function> functions;
    private final List<ExternFunction> externFunctions;

    public File(String name) {
        this.name = name;
        this.types = new ArrayList<>();
        this.classImplementations = new ArrayList<>();
        this.interfaceImplementations = new ArrayList<>();
        this.functions = new ArrayList<>();
        this.externFunctions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<TopLevelType> getTypes() {
        return types;
    }

    public List<ClassImplementation> getClassImplementations() {
        return classImplementations;
    }

    public List<InterfaceImplementation> getInterfaceImplementations() {
        return interfaceImplementations;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public List<ExternFunction> getExternFunctions() {
        return externFunctions;
    }
}
