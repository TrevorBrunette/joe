package pro.trevor.joe.program.program_enum;

import pro.trevor.joe.program.TopLevelType;
import pro.trevor.joe.program.type.NamedTypeReference;

import java.util.ArrayList;
import java.util.List;

public final class Enum extends TopLevelType {

    private final List<Variant> variants;

    public Enum(NamedTypeReference name) {
        super(name);
        this.variants = new ArrayList<>();
    }

    public List<Variant> getMembers() {
        return variants;
    }
}
