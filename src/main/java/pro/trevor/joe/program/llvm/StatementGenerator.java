package pro.trevor.joe.program.llvm;

import org.bytedeco.llvm.LLVM.*;
import pro.trevor.joe.program.Parameter;
import pro.trevor.joe.program.code.Function;
import pro.trevor.joe.program.code.Statements;
import pro.trevor.joe.program.type.ArrayTypeReference;
import pro.trevor.joe.program.type.Primitive;
import pro.trevor.joe.program.type.PrimitiveTypeReference;
import pro.trevor.joe.program.type.TypeReference;

import java.util.List;

import static org.bytedeco.llvm.global.LLVM.*;

public class StatementGenerator {

    private final Generator generator;
    private final ExpressionGenerator expressionGenerator;
    private int count;

    public StatementGenerator(Generator generator) {
        this.generator = generator;
        this.expressionGenerator = new ExpressionGenerator(generator, this);
        this.count = 0;
    }

    public int getStatementCount() {
        return count++;
    }

    private void variableDeclaration(Statements.VariableDeclaration variableDeclarationStatement) {
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

    private void variableInitialization(LLVMBasicBlockRef block, Statements.VariableInitialization variableInitializationStatement) {
        generator.typeAnalyzer.getContext().addLocalType(variableInitializationStatement.name(), variableInitializationStatement.type());
        switch (variableInitializationStatement.type()) {
            case PrimitiveTypeReference primitiveTypeReference -> {
                LLVMTypeRef type = generator.getLLVMType(primitiveTypeReference);
                LLVMValueRef alloca = LLVMBuildAlloca(generator.llvm.builder, type, variableInitializationStatement.name());
                generator.typeAnalyzer.getContext().addLocalStorageValue(variableInitializationStatement.name(), alloca);

                LLVMValueRef value = expressionGenerator.addExpression(block, variableInitializationStatement.value());
                LLVMValueRef castValue = Util.cast(generator, value, type);

                LLVMBuildStore(generator.llvm.builder, castValue, alloca);
            }
            case ArrayTypeReference arrayTypeReference -> {
                LLVMTypeRef type = generator.getLLVMType(arrayTypeReference);
                LLVMValueRef alloca = LLVMBuildAlloca(generator.llvm.builder, type, variableInitializationStatement.name());
                
                generator.typeAnalyzer.getContext().addLocalStorageValue(variableInitializationStatement.name(), alloca);
                LLVMValueRef value = expressionGenerator.addExpression(block, variableInitializationStatement.value());
                TypeReference valueType = generator.typeAnalyzer.getType(variableInitializationStatement.value()).orElseThrow();
                if (!arrayTypeReference.equals(valueType)) {
                    throw new IllegalStateException(String.format("Array types mismatch: %s != %s", arrayTypeReference, valueType));
                }
                LLVMBuildStore(generator.llvm.builder, value, alloca);
            }
            default -> throw new IllegalStateException("Unhandled declaration type: " + variableInitializationStatement.type().getClass().getSimpleName());
        }
    }

    private void returnStatement(LLVMBasicBlockRef block, Statements.Return returnStatement) {
        if (returnStatement.value() == null) {
            if (generator.currentFunction.getReturnType() instanceof PrimitiveTypeReference(Primitive primitive)) {
                if (primitive != Primitive.VOID) {
                    throw new IllegalStateException("Returning a value when expected return type is void");
                }
            }
            LLVMBuildRetVoid(generator.llvm.builder);
        } else {
            LLVMValueRef returnValue = expressionGenerator.addExpression(block, returnStatement.value());
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

    private void addStatement(LLVMBasicBlockRef block, Statements.Statement statement) {
        LLVMPositionBuilderAtEnd(generator.llvm.builder, block);
        switch (statement) {
            case Statements.Expression expressionStatement -> {
                expressionGenerator.addExpression(block, expressionStatement.expression());
            }
            case Statements.Return returnStatement -> {
                returnStatement(block, returnStatement);
            }
            case Statements.VariableDeclaration variableDeclarationStatement -> {
                variableDeclaration(variableDeclarationStatement);
            }
            case Statements.VariableInitialization variableInitializationStatement -> {
                variableInitialization(block, variableInitializationStatement);
            }
            case Statements.Empty empty -> {
                // Intentionally left blank
            }
            default -> throw new IllegalStateException("Unimplemented statement type: " + statement.getClass().getSimpleName());
        }
    }

    private void addStatements(LLVMBasicBlockRef block, List<Statements.Statement> statements) {
        this.count = 0;
        for (Statements.Statement statement : statements) {
            addStatement(block, statement);
        }
    }

    private void addParameters(Function function, LLVMValueRef llvmFunction) {
        int llvmFunctionParams = LLVMCountParams(llvmFunction);
        assert llvmFunctionParams == function.getParameters().size();
        for (int i = 0; i < llvmFunctionParams; ++i) {
            Parameter parameter = function.getParameters().get(i);
            LLVMValueRef value = LLVMGetParam(llvmFunction, i);

            LLVMTypeRef type = LLVMTypeOf(value);
            LLVMValueRef alloca = LLVMBuildAlloca(generator.llvm.builder, type, parameter.identifier());

            LLVMValueRef castValue = Util.cast(generator, value, type);
            LLVMBuildStore(generator.llvm.builder, castValue, alloca);

            generator.typeAnalyzer.getContext().addLocalType(parameter.identifier(), parameter.type());
            generator.typeAnalyzer.getContext().addLocalStorageValue(parameter.identifier(), alloca);
        }
    }

    public void addFunction(LLVMBasicBlockRef block, Function function, LLVMValueRef llvmFunction) {
        addParameters(function, llvmFunction);
        addStatements(block, function.getCodeBlock().statements());
    }

}
