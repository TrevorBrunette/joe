package pro.trevor.joe.program.llvm;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import pro.trevor.joe.program.TopLevelType;
import pro.trevor.joe.program.analyzer.TypeAnalyzer;
import pro.trevor.joe.program.program_class.Class;
import pro.trevor.joe.program.type.NamedTypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                return LLVMBuildIntCast(generator.llvm.builder, value, type, "int.cast." + generator.statementGenerator.getStatementCount());
            }
            case LLVMFloatTypeKind, LLVMDoubleTypeKind, LLVMHalfTypeKind -> {
                return LLVMBuildFPCast(generator.llvm.builder, value, type, "fp.cast." + generator.statementGenerator.getStatementCount());
            }
            default -> {
                throw new IllegalStateException("Unimplemented implicit cast for RHS type kind " + LLVMGetTypeKind(type));
            }
        }
    }

    public static LLVMValueRef ptrToInt(Generator generator, LLVMValueRef value, LLVMTypeRef type) {
        return LLVMBuildPtrToInt(generator.llvm.builder, value, type, "ptrtoint."  + generator.statementGenerator.getStatementCount());
    }

    public static LLVMValueRef intToPtr(Generator generator, LLVMValueRef value, LLVMTypeRef type) {
        return LLVMBuildIntToPtr(generator.llvm.builder, value, type, "inttoptr." + generator.statementGenerator.getStatementCount());
    }

    public static void declareClass(Generator generator, Class classType) {
        LLVMTypeRef structType = LLVMStructCreateNamed(generator.llvm.ctx, classType.getName().name());
        List<Class> inheritance = Util.getInheritance(classType, generator.typeAnalyzer).reversed();
        List<LLVMTypeRef> members = new ArrayList<>();

        for (Class parent : inheritance) {
            members.addAll(parent.getVariables().stream().map((v) -> generator.getLLVMType(v.type())).toList());
        }

        LLVMTypeRef[] memberTypes = members.toArray(new LLVMTypeRef[0]);
        PointerPointer<LLVMTypeRef> memberTypesPtr = new PointerPointer<>(memberTypes);
        LLVMStructSetBody(structType, memberTypesPtr, memberTypes.length, 0);
        generator.typeAnalyzer.getContext().addTypeRef(classType.getName().name(), structType);
    }

    public static List<Class> getInheritance(Class classType, TypeAnalyzer typeAnalyzer) {
        List<Class> output = new ArrayList<>();
        Class current = classType;
        while (current.getSuperclass() != null) {
            output.add(current);
            NamedTypeReference superClassName = current.getSuperclass();
            Optional<TopLevelType> mabeType = typeAnalyzer.getContext().getTopLevelType(superClassName.name());
            if (mabeType.isEmpty()) {
                throw new IllegalStateException("Cannot find super class '" + superClassName.name() + "'");
            }
            if (mabeType.get() instanceof Class parentClass) {
                current = parentClass;
            } else {
                throw new IllegalStateException("Cannot find super class '" + superClassName.name() + "'; instead found " + mabeType.get().getClass().getSimpleName());
            }
        }
        return output;
    }

}
