package pro.trevor.joe.program.type;

import java.util.List;

public record TupleTypeReference(List<TypeReference> types) implements TypeReference {

    public static final TupleTypeReference EMPTY = new TupleTypeReference(List.of());
}
