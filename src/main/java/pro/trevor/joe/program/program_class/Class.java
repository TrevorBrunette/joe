package pro.trevor.joe.program.program_class;

import pro.trevor.joe.program.TopLevelNode;

import java.util.ArrayList;
import java.util.List;

public class Class extends TopLevelNode {

    private final List<MemberVariable> variables;
    private final List<MemberFunction> functions;

    public Class(List<TopLevelNode> innerTopLevelNodes) {
        super(innerTopLevelNodes);
        this.variables = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void addVariable(MemberVariable variable) {
        variables.add(variable);
    }

    public void addFunction(MemberFunction function) {
        functions.add(function);
    }

    public List<MemberVariable> getVariables() {
        return variables;
    }

    public List<MemberFunction> getFunctions() {
        return functions;
    }
}
