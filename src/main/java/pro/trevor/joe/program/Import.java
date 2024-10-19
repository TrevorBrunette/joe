package pro.trevor.joe.program;

public class Import {

    private final Path path;
    private final String alias;

    public Import(Path path) {
        this.path = path;
        this.alias = path.elements().getLast();
    }

    public Import(Path path, String alias) {
        this.path = path;
        this.alias = alias;
    }

    public Path getPath() {
        return path;
    }

    public String getAlias() {
        return alias;
    }
}
