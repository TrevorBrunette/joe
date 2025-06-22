package pro.trevor.joe.util;

import java.util.Objects;

public class Pair<T1, T2> {

    private final T1 first;
    private final T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getLeft() {
        return first;
    }

    public T2 getRight() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair<?, ?> pair)) return false;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
