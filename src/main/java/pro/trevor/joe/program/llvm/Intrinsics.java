package pro.trevor.joe.program.llvm;

import pro.trevor.joe.program.type.ArrayTypeReference;
import pro.trevor.joe.program.type.Primitive;
import pro.trevor.joe.program.type.PrimitiveTypeReference;
import pro.trevor.joe.program.type.TypeReference;

public class Intrinsics {

    public static final String MALLOC_NAME = "malloc";
    public static final TypeReference MALLOC_ARG = new PrimitiveTypeReference(Primitive.U64);
    public static final TypeReference MALLOC_RET = new ArrayTypeReference(new PrimitiveTypeReference(Primitive.U8));

}
