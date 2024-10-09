package pro.trevor.joe.program.program_enum;

import pro.trevor.joe.program.TopLevelNode;

import java.util.ArrayList;
import java.util.List;

public class Enum extends TopLevelNode {

    private final List<EnumMember> members;

    public Enum(List<TopLevelNode> innerTopLevelNodes) {
        super(innerTopLevelNodes);
        this.members = new ArrayList<>();
    }

    public List<EnumMember> getMembers() {
        return members;
    }
}
