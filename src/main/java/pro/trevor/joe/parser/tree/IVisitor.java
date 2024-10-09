package pro.trevor.joe.parser.tree;

import pro.trevor.joe.parser.tree.declaration.*;
import pro.trevor.joe.parser.tree.expression.*;
import pro.trevor.joe.parser.tree.expression.literal.*;
import pro.trevor.joe.parser.tree.expression.binary.*;
import pro.trevor.joe.parser.tree.expression.unary.*;
import pro.trevor.joe.parser.tree.statement.*;

public interface IVisitor {
    // Declarations
    default void visit(Declaration declaration) {
        switch (declaration) {
            case ClassDeclaration x -> visit(x);
            case EnumDeclaration x -> visit(x);
            case EnumVariantDeclaration x -> visit(x);
            case FunctionDeclaration x -> visit(x);
            case FunctionStubDeclaration x -> visit(x);
            case InterfaceDeclaration x -> visit(x);
            case ParameterDeclaration x -> visit(x);
            case VariableDeclaration x -> visit(x);
            default -> throw new Error("Unhandled visiting of " + declaration.getClass().getSimpleName());
        }
    }

    default void visit(TypeMember typeMember) {
        if (typeMember instanceof Declaration declaration) {
            visit(declaration);
        } else {
            throw new Error("Unhandled visiting of " + typeMember.getClass().getSimpleName());
        }
    }

    void visit(ClassDeclaration classDeclaration);
    void visit(EnumDeclaration enumDeclaration);
    void visit(EnumVariantDeclaration enumVariantDeclaration);
    void visit(FunctionDeclaration functionDeclaration);
    void visit(FunctionStubDeclaration functionStubDeclaration);
    void visit(InterfaceDeclaration interfaceDeclaration);
    void visit(VariableDeclaration variableDeclaration);
    void visit(ParameterDeclaration parameterDeclaration);

    // Statements
    default void visit(IStatement statement) {
        switch (statement) {
            case Block x -> visit(x);
            case EmptyStatement x -> visit(x);
            case ExpressionStatement x -> visit(x);
            case IfStatement x -> visit(x);
            case ReturnStatement x -> visit(x);
            case VariableInitializationStatement x -> visit(x);
            case VariableDeclarationStatement x -> visit(x);
            case WhileStatement x -> visit(x);
            default -> throw new Error("Unhandled visiting of " + statement.getClass().getSimpleName());
        }
    }

    void visit(Block block);
    void visit(EmptyStatement emptyStatement);
    void visit(ExpressionStatement expressionStatement);
    void visit(IfStatement ifStatement);
    void visit(ReturnStatement returnStatement);
    void visit(VariableInitializationStatement variableInitializationStatement);
    void visit(VariableDeclarationStatement variableDeclarationStatement);
    void visit(WhileStatement whileStatement);

    // Expressions
    default void visit(Expression expression) {
        switch (expression) {
            // Literal expressions
            case BooleanExpression x -> visit(x);
            case CharExpression x -> visit(x);
            case FloatExpression x -> visit(x);
            case IntegerExpression x -> visit(x);
            case NullExpression x -> visit(x);
            case StringExpression x -> visit(x);
            case ThisExpression x -> visit(x);

            // Operation expressions
            case AdditionExpression x -> visit(x);
            case AssignmentExpression x -> visit(x);
            case BinaryAndExpression x -> visit(x);
            case BinaryInvertExpression x -> visit(x);
            case BinaryOrExpression x -> visit(x);
            case BinaryXorExpression x -> visit(x);
            case DivideExpression x -> visit(x);
            case EqualsExpression x -> visit(x);
            case GreaterThanExpression x -> visit(x);
            case GreaterThanOrEqualsExpression x -> visit(x);
            case LessThanExpression x -> visit(x);
            case LessThanOrEqualsExpression x -> visit(x);
            case LogicalAndExpression x -> visit(x);
            case LogicalInvertExpression x -> visit(x);
            case LogicalOrExpression x -> visit(x);
            case LogicalXorExpression x -> visit(x);
            case ModuloExpression x -> visit(x);
            case MultiplyExpression x -> visit(x);
            case NotEqualsExpression x -> visit(x);
            case ShiftLeftExpression x -> visit(x);
            case ShiftRightExpression x -> visit(x);
            case ShiftRightLogicalExpression x -> visit(x);
            case SubtractionExpression x -> visit(x);

            // Other expressions
            case ArrayIndexExpression x -> visit(x);
            case VariableExpression x -> visit(x);
            case MethodInvocationExpression x -> visit(x);
            case ObjectInstantiationExpression x -> visit(x);
            case VariableAccessExpression x -> visit(x);
            case WrappedExpression x -> visit(x);

            default -> throw new Error("Unhandled visiting of " + expression.getClass().getSimpleName());
        }
    }

    void visit(BooleanExpression booleanExpression);
    void visit(CharExpression charExpression);
    void visit(FloatExpression floatExpression);
    void visit(IntegerExpression integerExpression);
    void visit(NullExpression nullExpression);
    void visit(StringExpression stringExpression);
    void visit(ThisExpression thisExpression);
    void visit(AdditionExpression additionExpression);
    void visit(AssignmentExpression assignmentExpression);
    void visit(BinaryAndExpression binaryAndExpression);
    void visit(BinaryInvertExpression binaryInvertExpression);
    void visit(BinaryOrExpression binaryOrExpression);
    void visit(BinaryXorExpression binaryXorExpression);
    void visit(DivideExpression divideExpression);
    void visit(EqualsExpression equalsExpression);
    void visit(GreaterThanExpression greaterThanExpression);
    void visit(GreaterThanOrEqualsExpression greaterThanOrEqualsExpression);
    void visit(LessThanExpression lessThanExpression);
    void visit(LessThanOrEqualsExpression lessThanOrEqualsExpression);
    void visit(LogicalAndExpression logicalAndExpression);
    void visit(LogicalInvertExpression logicalInvertExpression);
    void visit(LogicalOrExpression logicalOrExpression);
    void visit(LogicalXorExpression logicalXorExpression);
    void visit(ModuloExpression moduloExpression);
    void visit(MultiplyExpression multiplyExpression);
    void visit(NotEqualsExpression notEqualsExpression);
    void visit(ShiftLeftExpression shiftLeftExpression);
    void visit(ShiftRightExpression shiftRightExpression);
    void visit(ShiftRightLogicalExpression shiftRightLogicalExpression);
    void visit(SubtractionExpression subtractionExpression);
    void visit(ArrayIndexExpression arrayIndexExpression);
    void visit(VariableExpression localVariableExpression);
    void visit(MethodInvocationExpression methodInvocationExpression);
    void visit(ObjectInstantiationExpression objectInstantiationExpression);
    void visit(VariableAccessExpression variableAccessExpression);
    void visit(WrappedExpression wrappedExpression);
}
