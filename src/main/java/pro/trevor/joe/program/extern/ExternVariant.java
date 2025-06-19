package pro.trevor.joe.program.extern;

import pro.trevor.joe.program.type.ArrayTypeReference;
import pro.trevor.joe.program.type.PrimitiveTypeReference;
import pro.trevor.joe.program.type.TupleTypeReference;
import pro.trevor.joe.program.type.TypeReference;

public enum ExternVariant {
    C;

    public String toExternalLinkageString(TypeReference type) throws ExternGenerationException {
        switch (this) {
            case C -> {
                switch (type) {
                    case PrimitiveTypeReference primitiveReference -> {
                        return switch (primitiveReference.primitive()) {
                            case VOID -> "void";
                            case BOOL -> "bool";
                            case U8 -> "uint8_t";
                            case U16 -> "uint16_t";
                            case U32 -> "uint32_t";
                            case U64 -> "uint64_t";
                            case I8 -> "int8_t";
                            case I16 -> "int16_t";
                            case I32 -> "int32_t";
                            case I64 -> "int64_t";
                            case F32 -> "float";
                            case F64 -> "double";
                            case U128, I128 -> throw new ExternGenerationException(this, "No compatible type");
                        };
                    }
                    case ArrayTypeReference arrayReference -> {
                        return toExternalLinkageString(arrayReference.contained()) + "*";
                    }
                    case TupleTypeReference tupleReference -> {
                        if (tupleReference.equals(TypeReference.UNIT_TYPE)) {
                            return "void";
                        } else {
                            throw new ExternGenerationException(this, "Unsupported linkage type TupleTypeReference");
                        }
                    }
                    default -> throw new ExternGenerationException(this, "Unexpected linkage type " + type.getClass().getSimpleName());
                }
            }
        }
        throw new ExternGenerationException(this, "Unexpected fallthrough with " + type.toString());
    }
}
