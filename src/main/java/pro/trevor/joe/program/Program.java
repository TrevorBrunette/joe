package pro.trevor.joe.program;

import pro.trevor.joe.program.program_class.Class;
import pro.trevor.joe.program.program_enum.Enum;
import pro.trevor.joe.program.program_interface.Interface;

import java.util.ArrayList;
import java.util.List;

public class Program {

    private final List<pro.trevor.joe.program.program_class.Class> classes;
    private final List<pro.trevor.joe.program.program_enum.Enum> enums;
    private final List<Interface> interfaces;

    public Program() {
        this.classes = new ArrayList<>();
        this.enums = new ArrayList<>();
        this.interfaces = new ArrayList<>();
    }

    public void addClass(Class clazz) {
        classes.add(clazz);
    }

    public void addEnum(Enum enumClass) {
        enums.add(enumClass);
    }

    public void addInterface(Interface interfaceClass) {
        interfaces.add(interfaceClass);
    }
}
