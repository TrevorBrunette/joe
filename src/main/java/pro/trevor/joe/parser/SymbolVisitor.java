package pro.trevor.joe.parser;

import pro.trevor.joe.tree.*;
import pro.trevor.joe.tree.declaration.*;
import pro.trevor.joe.tree.expression.*;
import pro.trevor.joe.tree.expression.binary.*;
import pro.trevor.joe.tree.expression.literal.*;
import pro.trevor.joe.tree.expression.unary.BinaryInvertExpression;
import pro.trevor.joe.tree.expression.unary.LogicalInvertExpression;
import pro.trevor.joe.tree.statement.*;

import java.util.HashMap;
import java.util.Map;

public class SymbolVisitor implements IVisitor {

    private final Map<Node, SymbolTable> symbolTable;
    private Node parent;

    public SymbolVisitor() {
        this.symbolTable = new HashMap<>();
        this.parent = null;
    }

    public Map<Node, SymbolTable> getSymbolTable() {
        return symbolTable;
    }

    private void put(Node node, SymbolInfo symbolInfo) {
        symbolTable.computeIfAbsent(node, k -> new SymbolTable());
        symbolTable.get(node).put(symbolInfo.getSymbol(), symbolInfo);
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        if (parent == null) {
            parent = classDeclaration;
        }
        put(parent, new SymbolInfo(classDeclaration.getIdentifier(), SymbolType.CLASS));
        Node previous = parent;
        parent = classDeclaration;
        for (ClassMember member : classDeclaration.getClassMembers()) {
            visit(member);
        }
        parent = previous;
    }

    @Override
    public void visit(EnumDeclaration enumDeclaration) {
        if (parent == null) {
            parent = enumDeclaration;
        }
        put(parent, new SymbolInfo(enumDeclaration.getIdentifier(), SymbolType.ENUM));
        Node previous = parent;
        parent = enumDeclaration;
        for (EnumMember member : enumDeclaration.getEnumMembers()) {
            visit(member);
        }
        parent = previous;
    }

    @Override
    public void visit(EnumVariantDeclaration enumVariantDeclaration) {
        put(parent, new SymbolVariableInfo(enumVariantDeclaration.getIdentifier(), ((Declaration) parent).getIdentifier()));
    }

    @Override
    public void visit(FunctionDeclaration functionDeclaration) {
        put(parent, new SymbolFunctionInfo(functionDeclaration.getIdentifier(), functionDeclaration.getReturnType()));
        Node previous = parent;
        parent = functionDeclaration;
        for (ParameterDeclaration declaration : functionDeclaration.getArguments()) {
            visit(declaration);
        }
        visit(functionDeclaration.getCode());
        parent = previous;
    }

    @Override
    public void visit(FunctionStubDeclaration functionStubDeclaration) {
        put(parent, new SymbolFunctionInfo(functionStubDeclaration.getIdentifier(), functionStubDeclaration.getReturnType()));
        Node previous = parent;
        parent = functionStubDeclaration;
        for (ParameterDeclaration declaration : functionStubDeclaration.getArguments()) {
            visit(declaration);
        }
        parent = previous;
    }

    @Override
    public void visit(InterfaceDeclaration interfaceDeclaration) {
        if (parent == null) {
            parent = interfaceDeclaration;
        }
        put(parent, new SymbolInfo(interfaceDeclaration.getIdentifier(), SymbolType.INTERFACE));
        Node previous = parent;
        parent = interfaceDeclaration;
        for (InterfaceMember member : interfaceDeclaration.getInterfaceMembers()) {
            visit(member);
        }
        parent = previous;
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {
        put(parent, new SymbolVariableInfo(variableDeclaration.getIdentifier(), variableDeclaration.getType()));
    }

    @Override
    public void visit(ParameterDeclaration parameterDeclaration) {
        put(parent, new SymbolVariableInfo(parameterDeclaration.getIdentifier(), parameterDeclaration.getType()));
    }

    @Override
    public void visit(Block block) {
        Node previous = parent;
        parent = block;
        for (IStatement statement : block.getStatements()) {
            visit(statement);
        }
        parent = previous;
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
        put(parent, new SymbolVariableInfo(variableInitializationStatement.getIdentifier(), variableInitializationStatement.getType()));
    }

    @Override
    public void visit(VariableDeclarationStatement variableDeclarationStatement) {
        put(parent, new SymbolVariableInfo(variableDeclarationStatement.getIdentifier(), variableDeclarationStatement.getType()));
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
    public void visit(VariableExpression localVariableExpression) {

    }

    @Override
    public void visit(MethodInvocationExpression methodInvocationExpression) {

    }

    @Override
    public void visit(ObjectInstantiationExpression objectInstantiationExpression) {

    }

    @Override
    public void visit(VariableAccessExpression variableAccessExpression) {

    }

    @Override
    public void visit(WrappedExpression wrappedExpression) {

    }
}
