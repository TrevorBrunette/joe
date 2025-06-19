package pro.trevor.joe.program.type;

import pro.trevor.joe.lexer.TokenType;
import pro.trevor.joe.parser.tree.Type;

public sealed interface TypeReference permits PrimitiveTypeReference, NamedTypeReference, ArrayTypeReference, TupleTypeReference {
    TypeReference UNIT_TYPE = TupleTypeReference.EMPTY;

    static TypeReference fromType(Type type) {
        TypeReference baseType;
        if (type.getType().isPrimitive()) {
            baseType = new PrimitiveTypeReference(Primitive.valueOf(type.getString().toUpperCase()));
        } else if (type.getType() == TokenType.IDENTIFIER) {
            baseType = new NamedTypeReference(type.getString());
        } else {
            // TODO handle tuple
            throw new IllegalArgumentException("Bad type: " + type.getType());
        }

        int arrayLevels = type.getArrayLevels();
        while (arrayLevels > 0) {
            baseType = new ArrayTypeReference(baseType);
            --arrayLevels;
        }

        return baseType;
    }
}
