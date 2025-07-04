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

    private LLVMBasicBlockRef block;
    private LLVMValueRef currentLlvmFunction;

    public StatementGenerator(Generator generator) {
        this.generator = generator;
        this.expressionGenerator = new ExpressionGenerator(generator, this);
        this.count = 0;
        this.block = null;
        this.currentLlvmFunction = null;
    }

    public int getStatementCount() {
        return count++;
    }

    private void newBlock(LLVMBasicBlockRef block) {
        this.block = block;
        LLVMPositionBuilderAtEnd(generator.llvm.builder, block);
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

    private void variableInitialization(Statements.VariableInitialization variableInitializationStatement) {
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

    private void returnStatement(Statements.Return returnStatement) {
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

    private void ifStatement(Statements.If ifStatement) {
        LLVMValueRef condition = expressionGenerator.addExpression(block, ifStatement.condition());
        LLVMBasicBlockRef thenBlock = LLVMAppendBasicBlockInContext(generator.llvm.ctx, currentLlvmFunction, "then." +  getStatementCount());
        LLVMBasicBlockRef endBlock = LLVMAppendBasicBlockInContext(generator.llvm.ctx, currentLlvmFunction, "end." +  getStatementCount());
        LLVMBuildCondBr(generator.llvm.builder, condition, thenBlock, endBlock);
        newBlock(thenBlock);
        addStatement(ifStatement.then());
        boolean thenTerminated = isBlockTerminated(block);
        if (!thenTerminated) {
            LLVMBuildBr(generator.llvm.builder, endBlock);
        }
        newBlock(endBlock);
    }

    private void ifElseStatement(Statements.IfElse ifElseStatement) {
        LLVMValueRef condition = expressionGenerator.addExpression(block, ifElseStatement.condition());
        LLVMBasicBlockRef thenBlock = LLVMAppendBasicBlockInContext(generator.llvm.ctx, currentLlvmFunction, "then." +  getStatementCount());
        LLVMBasicBlockRef elseBlock = LLVMAppendBasicBlockInContext(generator.llvm.ctx, currentLlvmFunction, "else." +  getStatementCount());
        LLVMBasicBlockRef endBlock = LLVMAppendBasicBlockInContext(generator.llvm.ctx, currentLlvmFunction, "end." +  getStatementCount());
        LLVMBuildCondBr(generator.llvm.builder, condition, thenBlock, elseBlock);
        newBlock(thenBlock);
        addStatement(ifElseStatement.then());

        boolean thenTerminated = isBlockTerminated(block);
        if (!thenTerminated) {
            LLVMBuildBr(generator.llvm.builder, endBlock);
        }
        newBlock(elseBlock);
        addStatement(ifElseStatement.elseStatement());
        boolean elseTerminated = isBlockTerminated(block);
        if (!elseTerminated) {
            LLVMBuildBr(generator.llvm.builder, endBlock);
        }

        if (!thenTerminated || !elseTerminated) {
            newBlock(endBlock);
        }
    }

    private void whileStatement(Statements.While whileStatement) {
        LLVMBasicBlockRef conditionBlock = LLVMAppendBasicBlockInContext(generator.llvm.ctx, currentLlvmFunction, "while." +  getStatementCount());
        LLVMBasicBlockRef loopBlock = LLVMAppendBasicBlockInContext(generator.llvm.ctx, currentLlvmFunction, "loop." +  getStatementCount());
        LLVMBasicBlockRef endBlock = LLVMAppendBasicBlockInContext(generator.llvm.ctx, currentLlvmFunction, "end." +  getStatementCount());
        LLVMBuildBr(generator.llvm.builder, conditionBlock);
        newBlock(conditionBlock);
        LLVMValueRef condition = expressionGenerator.addExpression(block, whileStatement.condition());
        LLVMBuildCondBr(generator.llvm.builder, condition, loopBlock, endBlock);
        newBlock(loopBlock);
        addStatement(whileStatement.body());
        LLVMBuildBr(generator.llvm.builder, conditionBlock);
        newBlock(endBlock);
    }

    private boolean isBlockTerminated(LLVMBasicBlockRef block) {
        LLVMValueRef lastInstruction = LLVMGetLastInstruction(block);
        if (lastInstruction == null || lastInstruction.isNull()) {
            return true;
        }
        LLVMValueRef returnInstruction = LLVMIsAReturnInst(lastInstruction);
        return returnInstruction != null;
    }

    private void addStatement(Statements.Statement statement) {
        switch (statement) {
            case Statements.Expression expressionStatement -> expressionGenerator.addExpression(block, expressionStatement.expression());
            case Statements.Return returnStatement -> returnStatement(returnStatement);
            case Statements.VariableDeclaration variableDeclarationStatement -> variableDeclaration(variableDeclarationStatement);
            case Statements.VariableInitialization variableInitializationStatement -> variableInitialization(variableInitializationStatement);
            case Statements.If ifStatement -> ifStatement(ifStatement);
            case Statements.IfElse ifElseStatement -> ifElseStatement(ifElseStatement);
            case Statements.While whileStatement -> whileStatement(whileStatement);
            case Statements.Block blockStatement -> {
                for (Statements.Statement aBlockStatement : blockStatement.statements()) {
                    addStatement(aBlockStatement);
                }
            }
            case Statements.Empty empty -> {
                // Intentionally left blank
            }
            default -> throw new IllegalStateException("Unimplemented statement type: " + statement.getClass().getSimpleName());
        }
    }

    private void addStatements(List<Statements.Statement> statements) {
        this.count = 0;
        for (Statements.Statement statement : statements) {
            addStatement(statement);
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

    private void addImplicitReturn(Function function, LLVMValueRef llvmFunction) {
        if (function.getReturnType() instanceof PrimitiveTypeReference primitiveTypeReference && primitiveTypeReference.primitive() == Primitive.VOID) {
            LLVMBasicBlockRef lastBlock = LLVMGetLastBasicBlock(llvmFunction);

            if (lastBlock == null) {
                lastBlock = LLVMGetEntryBasicBlock(llvmFunction);
            }

            if (lastBlock != null && isBlockTerminated(lastBlock)) {
                LLVMPositionBuilderAtEnd(generator.llvm.builder, lastBlock);
                LLVMBuildRetVoid(generator.llvm.builder);
            } else {
                System.err.println("Unable to fix function " + function.getIdentifier() + " implicit return");
                System.err.println("Attempting to add return anyways");
                LLVMBuildRetVoid(generator.llvm.builder);
            }
        }
    }

    public void addFunction(Function function, LLVMValueRef llvmFunction) {
        currentLlvmFunction = llvmFunction;

        newBlock(LLVMAppendBasicBlockInContext(generator.llvm.ctx, llvmFunction, "entry"));
        addParameters(function, llvmFunction);
        addStatements(function.getCodeBlock().statements());
        addImplicitReturn(function, llvmFunction);

        currentLlvmFunction = null;
        block = null;
    }

}
