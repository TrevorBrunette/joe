package pro.trevor.joe.program.type;

import pro.trevor.joe.program.Path;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record NamedTypeReference(Path path, String name) implements TypeReference {

    private static Path pathFromString(String path) {
        String[] parts = path.split("::");
        return new Path(Arrays.asList(parts).subList(0, parts.length - 1));
    }

    private static String nameFromString(String path) {
        String[] parts = path.split("::");
        return parts[parts.length - 1];
    }

    public NamedTypeReference(String path) {
        this(pathFromString(path), nameFromString(path));
    }

    public NamedTypeReference(List<String> path) {
        this(new Path(path.subList(0, path.size() - 1)), path.getLast());
    }

    public void addSection(String section) {
        path.elements().add(section);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NamedTypeReference that)) return false;
        return Objects.equals(path, that.path) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name);
    }
}
