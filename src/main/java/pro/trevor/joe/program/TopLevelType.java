package pro.trevor.joe.program;

import pro.trevor.joe.program.program_class.Class;
import pro.trevor.joe.program.program_enum.Enum;
import pro.trevor.joe.program.program_interface.Interface;
import pro.trevor.joe.program.type.NamedTypeReference;

public abstract class TopLevelType {
    private final NamedTypeReference name;
    public TopLevelType(NamedTypeReference name) {
        this.name = name;
    }

    public NamedTypeReference getName() {
        return name;
    }
}
