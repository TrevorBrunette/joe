package pro.trevor.joe.program.program_interface;

import pro.trevor.joe.program.TopLevelNode;

import java.util.ArrayList;
import java.util.List;

public class Interface extends TopLevelNode {

    private final List<FunctionDeclaration> functions;

    public Interface(List<TopLevelNode> innerTopLevelNodes) {
        super(innerTopLevelNodes);
        this.functions = new ArrayList<>();
    }

    public List<FunctionDeclaration> getFunctionDeclarations() {
        return functions;
    }
}
