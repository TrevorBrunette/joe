package pro.trevor.joe.program;

import pro.trevor.joe.parser.tree.AstVisitor;
import pro.trevor.joe.parser.tree.IStatement;
import pro.trevor.joe.parser.tree.declaration.*;
import pro.trevor.joe.parser.tree.expression.*;
import pro.trevor.joe.parser.tree.expression.binary.*;
import pro.trevor.joe.parser.tree.expression.literal.*;
import pro.trevor.joe.parser.tree.expression.unary.BinaryInvertExpression;
import pro.trevor.joe.parser.tree.expression.unary.LogicalInvertExpression;
import pro.trevor.joe.parser.tree.statement.*;
import pro.trevor.joe.program.code.Expressions;
import pro.trevor.joe.program.code.Function;
import pro.trevor.joe.program.code.InterfaceImplementation;
import pro.trevor.joe.program.code.Statements;
import pro.trevor.joe.program.extern.ExternFunction;
import pro.trevor.joe.program.extern.ExternVariant;
import pro.trevor.joe.program.program_class.Class;
import pro.trevor.joe.program.program_class.MemberVariable;
import pro.trevor.joe.program.program_enum.Enum;
import pro.trevor.joe.program.program_enum.Variant;
import pro.trevor.joe.program.program_interface.Interface;
import pro.trevor.joe.program.type.NamedTypeReference;
import pro.trevor.joe.program.type.TypeReference;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AstToFileVisitor implements AstVisitor {

    private final File file;
    private TopLevelType currentType;
    private Statements.Block currentBlock;
    private Expressions.Expression returnExpression;
    private Statements.Statement returnStatement;
    private InterfaceImplementation currentImpl;

    public AstToFileVisitor(String fileName) {
        this.file = new File(fileName);
        this.currentImpl = null;
        this.currentType = null;
        this.currentBlock = null;
        this.returnExpression = null;
        this.returnStatement = null;
    }

    public File getFile() {
        return file;
    }

    @Override
    public void visit(ImplDeclaration implDeclaration) {
        this.currentImpl = new InterfaceImplementation(new NamedTypeReference(implDeclaration.getInterfaceIdentifier()), new NamedTypeReference(implDeclaration.getClassIdentifier()));
        implDeclaration.getDeclarations().forEach(this::visit);
        file.getInterfaceImplementations().add(this.currentImpl);
        this.currentImpl = null;
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        this.currentType = new Class(new NamedTypeReference(classDeclaration.getIdentifier()), new NamedTypeReference(classDeclaration.getSuperClassName()));
        classDeclaration.getClassMembers().forEach(this::visit);
        file.getTypes().add(this.currentType);
        this.currentType = null;
    }

    @Override
    public void visit(EnumDeclaration enumDeclaration) {
        this.currentType = new Enum(new NamedTypeReference(enumDeclaration.getIdentifier()));
        enumDeclaration.getEnumMembers().forEach(this::visit);
        file.getTypes().add(this.currentType);
        this.currentType = null;
    }

    @Override
    public void visit(EnumVariantDeclaration enumVariantDeclaration) {
        Enum parent = (Enum) currentType;
        List<TypeReference> parameters = enumVariantDeclaration.getTypes().stream().map(TypeReference::fromType).toList();
        parent.getMembers().add(new Variant(enumVariantDeclaration.getIdentifier(), parameters));
    }

    @Override
    public void visit(FunctionDeclaration functionDeclaration) {
        Class parent = (Class) currentType;
        TypeReference returnType = TypeReference.fromType(functionDeclaration.getReturnType());
        List<Parameter> parameters = functionDeclaration.getArguments().stream().map(arg -> new Parameter(arg.getIdentifier(), TypeReference.fromType(arg.getType()))).toList();
        Function function = new Function(functionDeclaration.getAccess(), functionDeclaration.isStatic(), functionDeclaration.getIdentifier(), parameters, returnType);
        visit(functionDeclaration.getCode());
        function.getCodeBlock().statements().addAll(currentBlock.statements());
        currentBlock = null;
        if (parent != null) {
            parent.getImplementation().functions().add(function);
        } else if (currentImpl != null) {
            this.currentImpl.addFunction(function);
        } else {
            file.getFunctions().add(function);
        }
    }

    @Override
    public void visit(FunctionStubDeclaration functionStubDeclaration) {
        TypeReference returnType = TypeReference.fromType(functionStubDeclaration.getReturnType());
        if (functionStubDeclaration.isExtern()) {
            List<TypeReference> parameterTypes = functionStubDeclaration.getArguments().stream().map(arg -> TypeReference.fromType(arg.getType())).toList();
            ExternFunction function = new ExternFunction(functionStubDeclaration.getAccess(), ExternVariant.C, functionStubDeclaration.getIdentifier(), parameterTypes, returnType, functionStubDeclaration.isVarArg());
            file.getExternFunctions().add(function);
        } else {
            if (functionStubDeclaration.isVarArg()) {
                throw new Error("Only extern functions can have variable arguments");
            }

            Interface parent = (Interface) currentType;

            List<Parameter> parameters = functionStubDeclaration.getArguments().stream().map(arg -> new Parameter(arg.getIdentifier(), TypeReference.fromType(arg.getType()))).toList();
            pro.trevor.joe.program.program_interface.FunctionDeclaration function = new pro.trevor.joe.program.program_interface.FunctionDeclaration(functionStubDeclaration.getIdentifier(), returnType, parameters);
            parent.getFunctionDeclarations().add(function);
        }
    }

    @Override
    public void visit(InterfaceDeclaration interfaceDeclaration) {
        this.currentType = new Interface(new NamedTypeReference(interfaceDeclaration.getIdentifier()));
        interfaceDeclaration.getInterfaceMembers().forEach(this::visit);
        file.getTypes().add(this.currentType);
        this.currentType = null;
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {
        Class parent = (Class) currentType;
        parent.addVariable(new MemberVariable(variableDeclaration.getAccess(), variableDeclaration.isStatic(), variableDeclaration.isFinal(), TypeReference.fromType(variableDeclaration.getType()), variableDeclaration.getIdentifier()));
    }

    @Override
    public void visit(ParameterDeclaration parameterDeclaration) {
        // NOT VISITED
    }

    @Override
    public void visit(Block block) {
        Statements.Block oldBlock = null;
        if (currentBlock != null) {
            oldBlock = currentBlock;
        }
        this.currentBlock = new Statements.Block(new ArrayList<>());
        for (IStatement statement : block.getStatements()) {
            this.visit(statement);
            this.currentBlock.statements().add(returnStatement);
        }
        this.returnStatement = currentBlock;
        if (oldBlock != null) {
            this.currentBlock = oldBlock;
        }

    }

    @Override
    public void visit(EmptyStatement emptyStatement) {
        currentBlock.statements().add(new Statements.Empty());
    }

    @Override
    public void visit(ExpressionStatement expressionStatement) {
        this.visit(expressionStatement.getExpression());
        this.returnStatement = new Statements.Expression(returnExpression);
    }

    @Override
    public void visit(IfStatement ifStatement) {
        this.visit(ifStatement.getCondition());
        Expressions.Expression condition = returnExpression;
        this.visit(ifStatement.getIfTrue());
        Statements.Statement thenStatement = returnStatement;
        if (ifStatement.getIfFalse() == null) {
            this.returnStatement = new Statements.If(condition, thenStatement);
        } else {
            this.visit(ifStatement.getIfFalse());
            Statements.Statement elseStatement = returnStatement;
            this.returnStatement = new Statements.IfElse(condition, thenStatement, elseStatement);
        }
    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        this.visit(returnStatement.getToReturn());
        this.returnStatement = new Statements.Return(returnExpression);
    }

    @Override
    public void visit(VariableInitializationStatement variableInitializationStatement) {
        this.visit(variableInitializationStatement.getExpression());
        Expressions.Expression expression = returnExpression;
        this.returnStatement = new Statements.VariableInitialization(TypeReference.fromType(variableInitializationStatement.getType()), variableInitializationStatement.getIdentifier(), expression);
    }

    @Override
    public void visit(VariableDeclarationStatement variableDeclarationStatement) {
        this.returnStatement = new Statements.VariableDeclaration(TypeReference.fromType(variableDeclarationStatement.getType()), variableDeclarationStatement.getIdentifier());
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        this.visit(whileStatement.getCondition());
        Expressions.Expression condition = returnExpression;
        this.visit(whileStatement.getStatement());
        Statements.Statement statement = returnStatement;
        this.returnStatement = new Statements.While(condition, statement);
    }

    @Override
    public void visit(BooleanExpression booleanExpression) {
        this.returnExpression = new Expressions.Boolean(booleanExpression.getValue());
    }

    @Override
    public void visit(CharExpression charExpression) {
        this.returnExpression = new Expressions.Char(StringUtil.escape(charExpression.getValue()).charAt(0));
    }

    @Override
    public void visit(FloatExpression floatExpression) {
        this.returnExpression = new Expressions.Float(new BigDecimal(floatExpression.getValue()));
    }

    @Override
    public void visit(IntegerExpression integerExpression) {
        this.returnExpression = new Expressions.Integer(new BigInteger(integerExpression.getValue()));
    }

    @Override
    public void visit(NullExpression nullExpression) {
        this.returnExpression = new Expressions.Null();
    }

    @Override
    public void visit(StringExpression stringExpression) {
        this.returnExpression = new Expressions.String(StringUtil.escape(stringExpression.getValue()));
    }

    @Override
    public void visit(ThisExpression thisExpression) {
        this.returnExpression = new Expressions.This(currentType.getName());
    }

    @Override
    public void visit(AdditionExpression additionExpression) {
        this.visit(additionExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(additionExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.Addition(left, right);
    }

    @Override
    public void visit(AssignmentExpression assignmentExpression) {
        this.visit(assignmentExpression.getLeftOperand());
        Expressions.AssignableExpression left = (Expressions.AssignableExpression) returnExpression;
        this.visit(assignmentExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.Assignment(left, right);
    }

    @Override
    public void visit(BinaryAndExpression binaryAndExpression) {
        this.visit(binaryAndExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(binaryAndExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.BinaryAnd(left, right);
    }

    @Override
    public void visit(BinaryInvertExpression binaryInvertExpression) {
        this.visit(binaryInvertExpression.getOperand());
        Expressions.YieldingExpression op = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.BinaryInvert(op);
    }

    @Override
    public void visit(BinaryOrExpression binaryOrExpression) {
        this.visit(binaryOrExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(binaryOrExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.BinaryOr(left, right);
    }

    @Override
    public void visit(BinaryXorExpression binaryXorExpression) {
        this.visit(binaryXorExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(binaryXorExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.BinaryXor(left, right);
    }

    @Override
    public void visit(DivideExpression divideExpression) {
        this.visit(divideExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(divideExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.Divide(left, right);
    }

    @Override
    public void visit(EqualsExpression equalsExpression) {
        this.visit(equalsExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(equalsExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.Equals(left, right);
    }

    @Override
    public void visit(GreaterThanExpression greaterThanExpression) {
        this.visit(greaterThanExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(greaterThanExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.GreaterThan(left, right);
    }

    @Override
    public void visit(GreaterThanOrEqualsExpression greaterThanOrEqualsExpression) {
        this.visit(greaterThanOrEqualsExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(greaterThanOrEqualsExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.GreaterThanOrEquals(left, right);
    }

    @Override
    public void visit(LessThanExpression lessThanExpression) {
        this.visit(lessThanExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(lessThanExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.LessThan(left, right);
    }

    @Override
    public void visit(LessThanOrEqualsExpression lessThanOrEqualsExpression) {
        this.visit(lessThanOrEqualsExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(lessThanOrEqualsExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.LessThanOrEquals(left, right);
    }

    @Override
    public void visit(LogicalAndExpression logicalAndExpression) {
        this.visit(logicalAndExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(logicalAndExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.LogicalAnd(left, right);
    }

    @Override
    public void visit(LogicalInvertExpression logicalInvertExpression) {
        this.visit(logicalInvertExpression.getOperand());
        Expressions.YieldingExpression op = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.LogicalInvert(op);
    }

    @Override
    public void visit(LogicalOrExpression logicalOrExpression) {
        this.visit(logicalOrExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(logicalOrExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.LogicalOr(left, right);
    }

    @Override
    public void visit(LogicalXorExpression logicalXorExpression) {
        this.visit(logicalXorExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(logicalXorExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.LogicalXor(left, right);
    }

    @Override
    public void visit(ModuloExpression moduloExpression) {
        this.visit(moduloExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(moduloExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.Modulo(left, right);
    }

    @Override
    public void visit(MultiplyExpression multiplyExpression) {
        this.visit(multiplyExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(multiplyExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.Multiply(left, right);
    }

    @Override
    public void visit(NotEqualsExpression notEqualsExpression) {
        this.visit(notEqualsExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(notEqualsExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.NotEquals(left, right);
    }

    @Override
    public void visit(ShiftLeftExpression shiftLeftExpression) {
        this.visit(shiftLeftExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(shiftLeftExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.ShiftLeft(left, right);
    }

    @Override
    public void visit(ShiftRightExpression shiftRightExpression) {
        this.visit(shiftRightExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(shiftRightExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.ShiftRight(left, right);
    }

    @Override
    public void visit(ShiftRightLogicalExpression shiftRightLogicalExpression) {
        this.visit(shiftRightLogicalExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(shiftRightLogicalExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.ShiftRightLogical(left, right);
    }

    @Override
    public void visit(SubtractionExpression subtractionExpression) {
        this.visit(subtractionExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(subtractionExpression.getRightOperand());
        Expressions.YieldingExpression right = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.Subtraction(left, right);
    }

    @Override
    public void visit(ArrayIndexExpression arrayIndexExpression) {
        this.visit(arrayIndexExpression.getArray());
        Expressions.YieldingExpression array = (Expressions.YieldingExpression) returnExpression;
        this.visit(arrayIndexExpression.getIndex());
        Expressions.YieldingExpression index = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.ArrayIndex(array, index);
    }

    @Override
    public void visit(ArrayInstantiationExpression arrayInstantiationExpression) {
        TypeReference type = TypeReference.fromType(arrayInstantiationExpression.getType());
        this.visit(arrayInstantiationExpression.getSizeExpression());
        Expressions.YieldingExpression index = (Expressions.YieldingExpression) returnExpression;
        this.returnExpression = new Expressions.ArrayInstantiation(type, index);
    }

    @Override
    public void visit(VariableExpression variableExpression) {
        this.returnExpression = new Expressions.Variable(variableExpression.getIdentifier());
    }

    @Override
    public void visit(MethodInvocationExpression methodInvocationExpression) {
        this.visit(methodInvocationExpression.getMethod());
        Expressions.YieldingExpression object = (Expressions.YieldingExpression) returnExpression;
        List<Expressions.YieldingExpression> arguments = new ArrayList<>();
        for (Expression expression : methodInvocationExpression.getParameters()) {
            this.visit(expression);
            arguments.add((Expressions.YieldingExpression) returnExpression);
        }
        this.returnExpression = new Expressions.MethodInvocation(object, arguments);
    }

    @Override
    public void visit(ObjectInstantiationExpression objectInstantiationExpression) {
        List<Expressions.YieldingExpression> arguments = new ArrayList<>();
        for (Expression expression : objectInstantiationExpression.getParameters()) {
            this.visit(expression);
            arguments.add((Expressions.YieldingExpression) returnExpression);
        }
        this.returnExpression = new Expressions.ObjectInstantiation(new NamedTypeReference(objectInstantiationExpression.getType()), arguments);
    }

    @Override
    public void visit(VariableAccessExpression variableAccessExpression) {
        this.visit(variableAccessExpression.getLeftOperand());
        Expressions.YieldingExpression left = (Expressions.YieldingExpression) returnExpression;
        this.visit(variableAccessExpression.getRightOperand());
        Expressions.Expression right = returnExpression;
        this.returnExpression = new Expressions.VariableAccess(left, right);
    }

    @Override
    public void visit(WrappedExpression wrappedExpression) {
        visit(wrappedExpression.getExpression());
    }
}
