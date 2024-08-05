package pro.trevor.joe.parser;

import pro.trevor.joe.tree.IStatement;
import pro.trevor.joe.tree.IVisitor;
import pro.trevor.joe.tree.declaration.*;
import pro.trevor.joe.tree.expression.ArrayIndexExpression;
import pro.trevor.joe.tree.expression.LocalVariableExpression;
import pro.trevor.joe.tree.expression.VariableAccessExpression;
import pro.trevor.joe.tree.expression.literal.*;
import pro.trevor.joe.tree.expression.operation.*;
import pro.trevor.joe.tree.statement.*;

public class PrintVisitor implements IVisitor {

    private final StringBuilder sb;

    private int indentFactor;

    public PrintVisitor() {
        sb = new StringBuilder();
        indentFactor = 0;
    }

    private void printWithIndent(String string) {
        sb.repeat(' ', indentFactor * 2).append(string);
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        printWithIndent(classDeclaration.getAccess().name().toLowerCase());
        sb.append(' ');
        if (classDeclaration.isStatic()) {
            sb.append("static ");
        }
        if (classDeclaration.isFinal()) {
            sb.append("final ");
        }
        sb.append("class ").append(classDeclaration.getIdentifier().getName()).append('\n');
        ++indentFactor;
        for (MemberDeclaration declaration : classDeclaration.getMemberDeclarations()) {
            visit(declaration);
        }
        --indentFactor;
    }

    @Override
    public void visit(FunctionDeclaration functionDeclaration) {
        printWithIndent(functionDeclaration.getAccess().name().toLowerCase());
        sb.append(' ').append(functionDeclaration.getIdentifier().getName()).append('(');
        for (ParameterDeclaration x : functionDeclaration.getArguments()) {
            visit(x);
            sb.append(", ");
        }
        sb.append(") ").append(functionDeclaration.getType().toString()).append('\n');
        visit(functionDeclaration.getCode());
        printWithIndent("\n");
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {

    }

    @Override
    public void visit(ParameterDeclaration parameterDeclaration) {
        sb.append(parameterDeclaration.getType().toString()).append(' ');
        sb.append(parameterDeclaration.getIdentifier().getName());
    }

    @Override
    public void visit(Block block) {
        printWithIndent("{\n");
        ++indentFactor;
        for (IStatement statement : block.getStatements()) {
//            visit(statement);
            printWithIndent(statement.getClass().getSimpleName());
            sb.append('\n');
        }
        --indentFactor;
        printWithIndent("}");
    }

    @Override
    public void visit(EmptyStatement emptyStatement) {

    }

    @Override
    public void visit(ExpressionStatement expressionStatement) {

    }

    @Override
    public void visit(IfStatement ifStatement) {

    }

    @Override
    public void visit(ReturnStatement returnStatement) {

    }

    @Override
    public void visit(VariableInitializationStatement variableInitializationStatement) {

    }

    @Override
    public void visit(VariableDeclarationStatement variableDeclarationStatement) {

    }

    @Override
    public void visit(WhileStatement whileStatement) {

    }

    @Override
    public void visit(BooleanExpression booleanExpression) {

    }

    @Override
    public void visit(CharExpression charExpression) {

    }

    @Override
    public void visit(FloatExpression floatExpression) {

    }

    @Override
    public void visit(IntegerExpression integerExpression) {

    }

    @Override
    public void visit(NullExpression nullExpression) {

    }

    @Override
    public void visit(StringExpression stringExpression) {

    }

    @Override
    public void visit(ThisExpression thisExpression) {

    }

    @Override
    public void visit(AdditionExpression additionExpression) {

    }

    @Override
    public void visit(AssignmentExpression assignmentExpression) {

    }

    @Override
    public void visit(BinaryAndExpression binaryAndExpression) {

    }

    @Override
    public void visit(BinaryInvertExpression binaryInvertExpression) {

    }

    @Override
    public void visit(BinaryOrExpression binaryOrExpression) {

    }

    @Override
    public void visit(BinaryXorExpression binaryXorExpression) {

    }

    @Override
    public void visit(DivideExpression divideExpression) {

    }

    @Override
    public void visit(EqualsExpression equalsExpression) {

    }

    @Override
    public void visit(GreaterThanExpression greaterThanExpression) {

    }

    @Override
    public void visit(GreaterThanOrEqualsExpression greaterThanOrEqualsExpression) {

    }

    @Override
    public void visit(LessThanExpression lessThanExpression) {

    }

    @Override
    public void visit(LessThanOrEqualsExpression lessThanOrEqualsExpression) {

    }

    @Override
    public void visit(LogicalAndExpression logicalAndExpression) {

    }

    @Override
    public void visit(LogicalInvertExpression logicalInvertExpression) {

    }

    @Override
    public void visit(LogicalOrExpression logicalOrExpression) {

    }

    @Override
    public void visit(LogicalXorExpression logicalXorExpression) {

    }

    @Override
    public void visit(ModuloExpression moduloExpression) {

    }

    @Override
    public void visit(MultiplyExpression multiplyExpression) {

    }

    @Override
    public void visit(NotEqualsExpression notEqualsExpression) {

    }

    @Override
    public void visit(ShiftLeftExpression shiftLeftExpression) {

    }

    @Override
    public void visit(ShiftRightExpression shiftRightExpression) {

    }

    @Override
    public void visit(ShiftRightLogicalExpression shiftRightLogicalExpression) {

    }

    @Override
    public void visit(SubtractionExpression subtractionExpression) {

    }

    @Override
    public void visit(ArrayIndexExpression arrayIndexExpression) {

    }

    @Override
    public void visit(LocalVariableExpression localVariableExpression) {

    }

    @Override
    public void visit(VariableAccessExpression variableAccessExpression) {

    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
