package pro.trevor.joe.program.llvm;

import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class Util {

    public static LLVMValueRef cast(Generator generator, LLVMValueRef value, LLVMTypeRef type) {
        LLVMTypeRef valueType = LLVMTypeOf(value);
        if (type.isNull()) {
            throw new IllegalStateException("Value type is null for cast");
        }
        if (valueType.equals(type)) {
            return value;
        }
        switch (LLVMGetTypeKind(type)) {
            case LLVMIntegerTypeKind -> {
                return LLVMBuildIntCast(generator.llvm.builder, value, type, "int.cast." + generator.getStatementCount());
            }
            case LLVMFloatTypeKind, LLVMDoubleTypeKind, LLVMHalfTypeKind -> {
                return LLVMBuildFPCast(generator.llvm.builder, value, type, "fp.cast." + generator.getStatementCount());
            }
            default -> {
                throw new IllegalStateException("Unimplemented implicit cast for RHS type kind " + LLVMGetTypeKind(type));
            }
        }
    }

    public static LLVMValueRef ptrToInt(Generator generator, LLVMValueRef value, LLVMTypeRef type) {
        return LLVMBuildPtrToInt(generator.llvm.builder, value, type, "ptrtoint."  + generator.getStatementCount());
    }

    public static LLVMValueRef intToPtr(Generator generator, LLVMValueRef value, LLVMTypeRef type) {
        return LLVMBuildIntToPtr(generator.llvm.builder, value, type, "inttoptr." + generator.getStatementCount());
    }

}
