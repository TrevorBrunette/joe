package pro.trevor.joe.program.type;

public sealed interface TypeReference permits PrimitiveTypeReference, NamedTypeReference, ArrayTypeReference, TupleTypeReference {
    TypeReference UNIT_TYPE = TupleTypeReference.EMPTY;
}
