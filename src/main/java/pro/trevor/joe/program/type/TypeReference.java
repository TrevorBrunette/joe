package pro.trevor.joe.program.type;

public sealed interface TypeReference permits NamedTypeReference, TupleTypeReference {
    TypeReference UNIT_TYPE = TupleTypeReference.EMPTY;
}
