package pro.trevor.joe.program;

import java.util.Arrays;
import java.util.List;

public class TypeReference {

    public static final TypeReference UNIT_TYPE = new TypeReference(List.of("$UNIT"));

    private final Path path;
    private final String name;

    public TypeReference(String path) {
        String[] parts = path.split("::");
        this.name = parts[parts.length - 1];
        this.path = new Path(Arrays.asList(parts).subList(0, parts.length - 1));
    }

    public TypeReference(List<String> path) {
        this.name = path.getLast();
        this.path = new Path(path.subList(0, path.size() - 1));
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

}
