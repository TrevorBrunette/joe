package pro.trevor.joe.program;

import java.util.List;

public abstract class TopLevelNode {
    private final List<TopLevelNode> innerTopLevelNodes;
    public TopLevelNode(List<TopLevelNode> innerTopLevelNodes) {
        this.innerTopLevelNodes = innerTopLevelNodes;
    }

    public List<TopLevelNode> getInnerTopLevelNodes() {
        return innerTopLevelNodes;
    }
}
