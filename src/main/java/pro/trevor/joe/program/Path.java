package pro.trevor.joe.program;

import java.util.List;

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
}
