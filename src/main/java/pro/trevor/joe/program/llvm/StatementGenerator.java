package pro.trevor.joe.program.llvm;

import org.bytedeco.llvm.LLVM.*;
import pro.trevor.joe.program.code.Statements;
import pro.trevor.joe.program.type.ArrayTypeReference;
import pro.trevor.joe.program.type.Primitive;
import pro.trevor.joe.program.type.PrimitiveTypeReference;
import pro.trevor.joe.program.type.TypeReference;

import static org.bytedeco.llvm.global.LLVM.*;

public class StatementGenerator {

    public static void variableDeclaration(Generator generator, Statements.VariableDeclaration variableDeclarationStatement) {
        generator.typeAnalyzer.getContext().addLocalType(variableDeclarationStatement.name(), variableDeclarationStatement.type());
        switch (variableDeclarationStatement.type()) {
            case PrimitiveTypeReference primitiveTypeReference -> {
                LLVMTypeRef type = generator.getLLVMType(primitiveTypeReference);
                LLVMValueRef alloca = LLVMBuildAlloca(generator.llvm.builder, type, variableDeclarationStatement.name());
                generator.typeAnalyzer.getContext().addLocalStorageValue(variableDeclarationStatement.name(), alloca);
            }
            case ArrayTypeReference arrayTypeReference -> {
                LLVMTypeRef type = generator.getLLVMType(arrayTypeReference);
                LLVMValueRef alloca = LLVMBuildAlloca(generator.llvm.builder, type, variableDeclarationStatement.name());
                generator.typeAnalyzer.getContext().addLocalStorageValue(variableDeclarationStatement.name(), alloca);
            }
            default -> throw new IllegalStateException("Unhandled declaration type: " + variableDeclarationStatement.type().getClass().getSimpleName());
        }
    }

    public static void variableInitialization(Generator generator, LLVMBasicBlockRef block, Statements.VariableInitialization variableInitializationStatement) {
        generator.typeAnalyzer.getContext().addLocalType(variableInitializationStatement.name(), variableInitializationStatement.type());
        switch (variableInitializationStatement.type()) {
            case PrimitiveTypeReference primitiveTypeReference -> {
                LLVMTypeRef type = generator.getLLVMType(primitiveTypeReference);
                LLVMValueRef alloca = LLVMBuildAlloca(generator.llvm.builder, type, variableInitializationStatement.name());
                generator.typeAnalyzer.getContext().addLocalStorageValue(variableInitializationStatement.name(), alloca);

                LLVMValueRef value = generator.addExpression(block, variableInitializationStatement.value());
                LLVMValueRef castValue = Util.cast(generator, value, type);

                LLVMBuildStore(generator.llvm.builder, castValue, alloca);
            }
            case ArrayTypeReference arrayTypeReference -> {
                LLVMTypeRef type = generator.getLLVMType(arrayTypeReference);
                LLVMValueRef alloca = LLVMBuildAlloca(generator.llvm.builder, type, variableInitializationStatement.name());
                
                generator.typeAnalyzer.getContext().addLocalStorageValue(variableInitializationStatement.name(), alloca);
                LLVMValueRef value = generator.addExpression(block, variableInitializationStatement.value());
                TypeReference valueType = generator.typeAnalyzer.getType(variableInitializationStatement.value()).orElseThrow();
                if (!arrayTypeReference.equals(valueType)) {
                    throw new IllegalStateException(String.format("Array types mismatch: %s != %s", arrayTypeReference, valueType));
                }
                LLVMBuildStore(generator.llvm.builder, value, alloca);
            }
            default -> throw new IllegalStateException("Unhandled declaration type: " + variableInitializationStatement.type().getClass().getSimpleName());
        }
    }

    public static void returnStatement(Generator generator, LLVMBasicBlockRef block, Statements.Return returnStatement) {
        if (returnStatement.value() == null) {
            if (generator.currentFunction.getReturnType() instanceof PrimitiveTypeReference(Primitive primitive)) {
                if (primitive != Primitive.VOID) {
                    throw new IllegalStateException("Returning a value when expected return type is void");
                }
            }
            LLVMBuildRetVoid(generator.llvm.builder);
        } else {
            LLVMValueRef returnValue = generator.addExpression(block, returnStatement.value());
            if (generator.currentFunction.getReturnType() instanceof PrimitiveTypeReference primitiveTypeReference) {
                Primitive primitive = primitiveTypeReference.primitive();
                if (primitive == Primitive.VOID) {
                    throw new IllegalStateException("Returning void when expected return type is: " + primitive.name());
                } else {
                    LLVMTypeRef type = generator.getLLVMType(primitiveTypeReference);
                    returnValue = Util.cast(generator, returnValue, type);
                }
            }
            LLVMBuildRet(generator.llvm.builder, returnValue);
        }
    }

}
