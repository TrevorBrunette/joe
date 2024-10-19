package pro.trevor.joe.program;

import java.util.List;
import java.util.Objects;

public record Path(List<String> elements) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Path path)) return false;

        return Objects.equals(elements, path.elements);
    }

}
