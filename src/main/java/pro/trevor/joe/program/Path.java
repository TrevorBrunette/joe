package pro.trevor.joe.program;

import java.util.List;
import java.util.Objects;

public class Path {
    private final List<String> elements;

    public Path(String path) {
        elements = List.of(path.split("::"));
    }

    public Path(List<String> elements) {
        this.elements = elements;
    }

    public List<String> getElements() {
        return elements;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Path path)) return false;

        return Objects.equals(elements, path.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elements);
    }
}
