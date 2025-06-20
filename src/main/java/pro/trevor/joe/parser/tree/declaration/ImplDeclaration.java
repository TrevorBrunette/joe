package pro.trevor.joe.parser.tree.declaration;

import pro.trevor.joe.lexer.Location;

import java.util.ArrayList;
import java.util.List;

public class ImplDeclaration extends TopLevelDeclaration {

    private final String interfaceIdentifier;
    private final String classIdentifier;
    private final List<FunctionDeclaration> functions;

    public ImplDeclaration(Location location, String interfaceIdentifier, String classIdentifier) {
        super(location, interfaceIdentifier + ":" + classIdentifier, Access.PUBLIC);
        this.interfaceIdentifier = interfaceIdentifier;
        this.classIdentifier = classIdentifier;
        this.functions = new ArrayList<>();
    }

    public void addDeclaration(FunctionDeclaration implMember) {
        functions.add(implMember);
    }

    public List<FunctionDeclaration> getDeclarations() {
        return functions;
    }

    public String getClassIdentifier() {
        return classIdentifier;
    }

    public String getInterfaceIdentifier() {
        return interfaceIdentifier;
    }
}
