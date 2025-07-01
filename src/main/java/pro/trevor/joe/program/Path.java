package pro.trevor.joe.program;

import java.util.List;
import java.util.Objects;

public record Path(List<String> elements) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Path path)) return false;
        return Objects.equals(elements, path.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elements);
    }
}
