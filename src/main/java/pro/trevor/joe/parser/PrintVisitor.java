package pro.trevor.joe.parser;

import pro.trevor.joe.tree.IStatement;
import pro.trevor.joe.tree.IVisitor;
import pro.trevor.joe.tree.declaration.*;
import pro.trevor.joe.tree.expression.*;
import pro.trevor.joe.tree.expression.binary.*;
import pro.trevor.joe.tree.expression.literal.*;
import pro.trevor.joe.tree.expression.unary.BinaryInvertExpression;
import pro.trevor.joe.tree.expression.unary.LogicalInvertExpression;
import pro.trevor.joe.tree.statement.*;

import java.util.List;

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
        printWithIndent("{");
        ++indentFactor;
        for (MemberDeclaration declaration : classDeclaration.getMemberDeclarations()) {
            sb.append("\n");
            visit(declaration);
        }
        --indentFactor;
        sb.append('\n');
        printWithIndent("}");
    }

    @Override
    public void visit(FunctionDeclaration functionDeclaration) {
        printWithIndent(functionDeclaration.getAccess().name().toLowerCase());
        sb.append(' ').append(functionDeclaration.getIdentifier().getName()).append('(');
        for (ParameterDeclaration x : functionDeclaration.getArguments()) {
            visit(x);
            sb.append(", ");
        }
        sb.append(") ").append(functionDeclaration.getReturnType().toString()).append(' ');
        visit(functionDeclaration.getCode());
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {
        printWithIndent(variableDeclaration.getAccess().name().toLowerCase());
        sb.append(' ').append(variableDeclaration.getType().toString());
        sb.append(' ').append(variableDeclaration.getIdentifier().toString()).append(";");
    }

    @Override
    public void visit(ParameterDeclaration parameterDeclaration) {
        sb.append(parameterDeclaration.getType().toString()).append(' ');
        sb.append(parameterDeclaration.getIdentifier().getName());
    }

    @Override
    public void visit(Block block) {
        sb.append('\n');
        printWithIndent("{");
        ++indentFactor;
        for (IStatement statement : block.getStatements()) {
            sb.append('\n');
            visit(statement);
        }
        --indentFactor;
        sb.append('\n');
        printWithIndent("}");
    }

    @Override
    public void visit(EmptyStatement emptyStatement) {
        printWithIndent(";");
    }

    @Override
    public void visit(ExpressionStatement expressionStatement) {
        printWithIndent("");
        visit(expressionStatement.getExpression());
        sb.append(";");
    }

    @Override
    public void visit(IfStatement ifStatement) {
        printWithIndent("if (");
        visit(ifStatement.getCondition());
        sb.append(") ");
        visit(ifStatement.getIfTrue());
        if (ifStatement.getIfFalse() != null) {
            sb.append(" else ");
            visit(ifStatement.getIfFalse());
        }
    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        printWithIndent("return ");
        visit(returnStatement.getToReturn());
        sb.append(";");
    }

    @Override
    public void visit(VariableInitializationStatement variableInitializationStatement) {
        printWithIndent(variableInitializationStatement.getType().toString());
        sb.append(' ').append(variableInitializationStatement.getIdentifier().toString()).append(" = ");
        visit(variableInitializationStatement.getExpression());
        sb.append(";");
    }

    @Override
    public void visit(VariableDeclarationStatement variableDeclarationStatement) {
        printWithIndent(variableDeclarationStatement.getType().toString());
        sb.append(' ').append(variableDeclarationStatement.getIdentifier().toString()).append(";");
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        printWithIndent("if (");
        visit(whileStatement.getCondition());
        sb.append(") ");
        visit(whileStatement.getStatement());
    }

    @Override
    public void visit(BooleanExpression booleanExpression) {
        sb.append(booleanExpression.getValue());
    }

    @Override
    public void visit(CharExpression charExpression) {
        sb.append('\'').append(charExpression.getValue()).append('\'');
    }

    @Override
    public void visit(FloatExpression floatExpression) {
        sb.append(floatExpression.getValue());
    }

    @Override
    public void visit(IntegerExpression integerExpression) {
        sb.append(integerExpression.getValue());
    }

    @Override
    public void visit(NullExpression nullExpression) {
        sb.append("null");
    }

    @Override
    public void visit(StringExpression stringExpression) {
        sb.append('"').append(stringExpression.getValue()).append('"');
    }

    @Override
    public void visit(ThisExpression thisExpression) {
        sb.append("this");
    }

    @Override
    public void visit(AdditionExpression additionExpression) {
        visit(additionExpression.getLeftOperand());
        sb.append(" + ");
        visit(additionExpression.getRightOperand());
    }

    @Override
    public void visit(AssignmentExpression assignmentExpression) {
        visit(assignmentExpression.getLeftOperand());
        sb.append(" = ");
        visit(assignmentExpression.getRightOperand());
    }

    @Override
    public void visit(BinaryAndExpression binaryAndExpression) {
        visit(binaryAndExpression.getLeftOperand());
        sb.append(" & ");
        visit(binaryAndExpression.getRightOperand());
    }

    @Override
    public void visit(BinaryInvertExpression binaryInvertExpression) {
        sb.append('~');
        visit(binaryInvertExpression.getOperand());
    }

    @Override
    public void visit(BinaryOrExpression binaryOrExpression) {
        visit(binaryOrExpression.getLeftOperand());
        sb.append(" | ");
        visit(binaryOrExpression.getRightOperand());
    }

    @Override
    public void visit(BinaryXorExpression binaryXorExpression) {
        visit(binaryXorExpression.getLeftOperand());
        sb.append(" ^ ");
        visit(binaryXorExpression.getRightOperand());
    }

    @Override
    public void visit(DivideExpression divideExpression) {
        visit(divideExpression.getLeftOperand());
        sb.append(" / ");
        visit(divideExpression.getRightOperand());
    }

    @Override
    public void visit(EqualsExpression equalsExpression) {
        visit(equalsExpression.getLeftOperand());
        sb.append(" == ");
        visit(equalsExpression.getRightOperand());
    }

    @Override
    public void visit(GreaterThanExpression greaterThanExpression) {
        visit(greaterThanExpression.getLeftOperand());
        sb.append(" > ");
        visit(greaterThanExpression.getRightOperand());
    }

    @Override
    public void visit(GreaterThanOrEqualsExpression greaterThanOrEqualsExpression) {
        visit(greaterThanOrEqualsExpression.getLeftOperand());
        sb.append(" >= ");
        visit(greaterThanOrEqualsExpression.getRightOperand());
    }

    @Override
    public void visit(LessThanExpression lessThanExpression) {
        visit(lessThanExpression.getLeftOperand());
        sb.append(" < ");
        visit(lessThanExpression.getRightOperand());
    }

    @Override
    public void visit(LessThanOrEqualsExpression lessThanOrEqualsExpression) {
        visit(lessThanOrEqualsExpression.getLeftOperand());
        sb.append(" <= ");
        visit(lessThanOrEqualsExpression.getRightOperand());
    }

    @Override
    public void visit(LogicalAndExpression logicalAndExpression) {
        visit(logicalAndExpression.getLeftOperand());
        sb.append(" && ");
        visit(logicalAndExpression.getRightOperand());
    }

    @Override
    public void visit(LogicalInvertExpression logicalInvertExpression) {
        sb.append("!");
        visit(logicalInvertExpression.getOperand());
    }

    @Override
    public void visit(LogicalOrExpression logicalOrExpression) {
        visit(logicalOrExpression.getLeftOperand());
        sb.append(" || ");
        visit(logicalOrExpression.getRightOperand());
    }

    @Override
    public void visit(LogicalXorExpression logicalXorExpression) {
        visit(logicalXorExpression.getLeftOperand());
        sb.append(" ^^ ");
        visit(logicalXorExpression.getRightOperand());
    }

    @Override
    public void visit(ModuloExpression moduloExpression) {
        visit(moduloExpression.getLeftOperand());
        sb.append(" % ");
        visit(moduloExpression.getRightOperand());
    }

    @Override
    public void visit(MultiplyExpression multiplyExpression) {
        visit(multiplyExpression.getLeftOperand());
        sb.append(" * ");
        visit(multiplyExpression.getRightOperand());
    }

    @Override
    public void visit(NotEqualsExpression notEqualsExpression) {
        visit(notEqualsExpression.getLeftOperand());
        sb.append(" != ");
        visit(notEqualsExpression.getRightOperand());
    }

    @Override
    public void visit(ShiftLeftExpression shiftLeftExpression) {
        visit(shiftLeftExpression.getLeftOperand());
        sb.append(" << ");
        visit(shiftLeftExpression.getRightOperand());
    }

    @Override
    public void visit(ShiftRightExpression shiftRightExpression) {
        visit(shiftRightExpression.getLeftOperand());
        sb.append(" >> ");
        visit(shiftRightExpression.getRightOperand());
    }

    @Override
    public void visit(ShiftRightLogicalExpression shiftRightLogicalExpression) {
        visit(shiftRightLogicalExpression.getLeftOperand());
        sb.append(" >>> ");
        visit(shiftRightLogicalExpression.getRightOperand());
    }

    @Override
    public void visit(SubtractionExpression subtractionExpression) {
        visit(subtractionExpression.getLeftOperand());
        sb.append(" - ");
        visit(subtractionExpression.getRightOperand());
    }

    @Override
    public void visit(ArrayIndexExpression arrayIndexExpression) {
        visit(arrayIndexExpression.getArray());
        sb.append('[');
        visit(arrayIndexExpression.getIndex());
        sb.append(']');
    }

    @Override
    public void visit(VariableExpression localVariableExpression) {
        sb.append(localVariableExpression.getIdentifier());
    }

    @Override
    public void visit(MethodInvocationExpression methodInvocationExpression) {
        sb.append(methodInvocationExpression.getMethod()).append("(");
        List<Expression> parameters = methodInvocationExpression.getParameters();
        if (!parameters.isEmpty()) {
            visit(parameters.getFirst());
            for (int i = 1; i < parameters.size(); ++i) {
                sb.append(',');
                visit(parameters.get(i));
            }
        }
        sb.append(')');
    }

    @Override
    public void visit(ObjectInstantiationExpression objectInstantiationExpression) {
        sb.append("new ").append(objectInstantiationExpression.getType().toString()).append("(");
        List<Expression> parameters = objectInstantiationExpression.getParameters();
        if (!parameters.isEmpty()) {
            visit(parameters.getFirst());
            for (int i = 1; i < parameters.size(); ++i) {
                sb.append(',');
                visit(parameters.get(i));
            }
        }
        sb.append(')');
    }

    @Override
    public void visit(VariableAccessExpression variableAccessExpression) {
        visit(variableAccessExpression.getLeftOperand());
        sb.append('.');
        visit(variableAccessExpression.getRightOperand());
    }

    @Override
    public void visit(WrappedExpression wrappedExpression) {
        sb.append('(');
        visit(wrappedExpression.getExpression());
        sb.append(')');
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
