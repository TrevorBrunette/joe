package pro.trevor.joe.program;

import pro.trevor.joe.parser.tree.IVisitor;
import pro.trevor.joe.parser.tree.declaration.*;
import pro.trevor.joe.parser.tree.expression.*;
import pro.trevor.joe.parser.tree.expression.binary.*;
import pro.trevor.joe.parser.tree.expression.literal.*;
import pro.trevor.joe.parser.tree.expression.unary.BinaryInvertExpression;
import pro.trevor.joe.parser.tree.expression.unary.LogicalInvertExpression;
import pro.trevor.joe.parser.tree.statement.*;

public class ProgramVisitor implements IVisitor {

    private Program program;

    public ProgramVisitor() {

    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {

    }

    @Override
    public void visit(EnumDeclaration enumDeclaration) {

    }

    @Override
    public void visit(EnumVariantDeclaration enumVariantDeclaration) {

    }

    @Override
    public void visit(FunctionDeclaration functionDeclaration) {

    }

    @Override
    public void visit(FunctionStubDeclaration functionStubDeclaration) {

    }

    @Override
    public void visit(InterfaceDeclaration interfaceDeclaration) {

    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {

    }

    @Override
    public void visit(ParameterDeclaration parameterDeclaration) {

    }

    @Override
    public void visit(Block block) {

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
