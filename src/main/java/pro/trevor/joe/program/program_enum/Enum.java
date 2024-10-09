package pro.trevor.joe.program.program_enum;

import pro.trevor.joe.program.TopLevelNode;

import java.util.ArrayList;
import java.util.List;

public class Enum extends TopLevelNode {

    private final List<Variant> variants;

    public Enum(List<TopLevelNode> innerTopLevelNodes) {
        super(innerTopLevelNodes);
        this.variants = new ArrayList<>();
    }

    public List<Variant> getMembers() {
        return variants;
    }
}
