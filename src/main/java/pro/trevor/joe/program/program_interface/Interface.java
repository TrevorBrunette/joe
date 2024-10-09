package pro.trevor.joe.program.program_interface;

import pro.trevor.joe.program.TopLevelNode;

import java.util.ArrayList;
import java.util.List;

public class Interface extends TopLevelNode {

    private final List<InterfaceMember> members;

    public Interface(List<TopLevelNode> innerTopLevelNodes) {
        super(innerTopLevelNodes);
        this.members = new ArrayList<>();
    }

    public List<InterfaceMember> getMembers() {
        return members;
    }
}
