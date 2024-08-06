package pro.trevor.joe.parser;

import pro.trevor.joe.lexer.Lexer;
import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.lexer.Token;
import pro.trevor.joe.lexer.TokenType;
import pro.trevor.joe.tree.IStatement;
import pro.trevor.joe.tree.Program;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.declaration.*;
import pro.trevor.joe.tree.expression.*;
import pro.trevor.joe.tree.expression.binary.*;
import pro.trevor.joe.tree.expression.literal.CharExpression;
import pro.trevor.joe.tree.expression.literal.FloatExpression;
import pro.trevor.joe.tree.expression.literal.IntegerExpression;
import pro.trevor.joe.tree.expression.literal.StringExpression;
import pro.trevor.joe.type.ReturnType;
import pro.trevor.joe.type.Type;
import pro.trevor.joe.tree.expression.unary.BinaryInvertExpression;
import pro.trevor.joe.tree.expression.unary.LogicalInvertExpression;
import pro.trevor.joe.tree.statement.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final Lexer lexer;
    private final List<ParseException> errors;
    private final Program program;

    private Token token;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        this.program = new Program(new Location(1, 1));
        consumeMaybeEof();
    }


    /*
     * [public|private|protected] [static] [final] class $identifier { MemberDeclaration* }
     */
    public ClassDeclaration parseClass(ClassDeclaration parent) throws ParseException {

        Access access = consumeAccessIfPresent();
        boolean isStatic = consumeStaticIfPresent();
        boolean isFinal = consumeFinalIfPresent();

        return parseClass(parent, access, isStatic, isFinal);
    }

    /*
     * class $identifier { MemberDeclaration* }
     */
    private ClassDeclaration parseClass(ClassDeclaration parent, Access access, boolean isStatic, boolean isFinal) throws ParseException {
        Token classToken = expectAndConsume(TokenType.CLASS);
        Token classNameToken = expectAndConsume(TokenType.IDENTIFIER);

        ClassDeclaration classDeclaration = new ClassDeclaration(classToken.getBeginLocation(), new Symbol(parent, classNameToken.getText()), null, access, isStatic, isFinal);

        expectAndConsume(TokenType.LBRACE);

        while (token.getType() != TokenType.RBRACE) {
            classDeclaration.addMemberDeclaration(parseMemberDeclaration(classDeclaration));
        }

        expectAndConsumeMaybeEof(TokenType.RBRACE);
        return classDeclaration;
    }

    /*
     * (
     *   [public|private|protected]
     *   [static]
     *   [final]
     *   (
     *     class $identifier { MemberDeclaration* } |
     *     fn $identifier([ParameterDeclaration (, ParameterDeclaration)*]) $type { Statement* } |
     *     $type $identifier;
     *   )
     * *)
     *
     */
    private MemberDeclaration parseMemberDeclaration(ClassDeclaration parent) throws ParseException {
        Access access = consumeAccessIfPresent();
        boolean isStatic = consumeStaticIfPresent();
        boolean isFinal = consumeFinalIfPresent();

        MemberDeclaration declaration;

        if (token.getType() == TokenType.CLASS) {
            // Inner class declaration
            declaration = parseClass(parent, access, isStatic, isFinal);
        } else if (token.getType() == TokenType.FN) {
            // Member function declaration
            Location beginning = token.getBeginLocation();
            consume();
            Token identifierToken = expectAndConsume(TokenType.IDENTIFIER);

            FunctionDeclaration functionDeclaration = new FunctionDeclaration(beginning, new Symbol(parent, identifierToken.getText()), parent, isStatic, isFinal, null, new ArrayList<>(), null);

            expectAndConsume(TokenType.LPAREN);
            int number = 0;
            if (token.getType() != TokenType.RPAREN) {
                do {
                    Token parameterType = token;
                    Token parameterIdentifier = expectAndConsume(TokenType.IDENTIFIER);
                    functionDeclaration.getArguments().add(new ParameterDeclaration(parameterType.getBeginLocation(), new Symbol(functionDeclaration, parameterType.getText()), new Symbol(functionDeclaration, parameterIdentifier.getText()), functionDeclaration, number++));
                    if (token.getType() == TokenType.COMMA) {
                        consume();
                    }
                }
                while (token.getType() != TokenType.RPAREN);
            }
            expectAndConsume(TokenType.RPAREN);

            Token typeToken = token;
            Block code = new Block(token.getBeginLocation());

            consume();
            expectAndConsume(TokenType.LBRACE);
            while (token.getType() != TokenType.RBRACE) {
                code.addStatement(parseStatement(parent));
            }
            expectAndConsume(TokenType.RBRACE);

            functionDeclaration.setReturnType(new Symbol(parent, typeToken.getText()));
            functionDeclaration.setCode(code);
            declaration = functionDeclaration;
        } else if (token.getType() == TokenType.IDENTIFIER || token.getType().isPrimitive()){
            // Member variable declaration
            Token typeToken = token;
            Token identifierToken = consume();
            consume();
            expectAndConsume(TokenType.SEMICOLON);
            declaration = new VariableDeclaration(typeToken.getBeginLocation(), new Symbol(parent, identifierToken.getText()), parent, access, isStatic, isFinal, new Symbol(parent, typeToken.getText()));
        } else {
            throw new ParseException(token.getBeginLocation(), new TokenType[]{TokenType.CLASS, TokenType.FN, TokenType.IDENTIFIER}, token);
        }

        return declaration;
    }

    private Block parseCodeBlock(MemberDeclaration parent) throws ParseException {
        Block block = new Block(token.getBeginLocation());

        expectAndConsume(TokenType.LBRACE);
        while (token.getType() != TokenType.RBRACE) {
            block.addStatement(parseStatement(parent));
        }
        expectAndConsume(TokenType.RBRACE);

        return block;
    }

    private IStatement parseStatement(MemberDeclaration parent) throws ParseException {
        IStatement statement;
        switch (token.getType()) {
            case LBRACE -> {
                statement = parseCodeBlock(parent);
            }
            case SEMICOLON -> {
                statement = new EmptyStatement(token.getBeginLocation());
                consume();
            }
            case IF -> {
                Token ifToken = token;
                consume();
                expectAndConsume(TokenType.LPAREN);
                Expression condition = parseExpression(parent);
                expectAndConsume(TokenType.RPAREN);
                IStatement trueStatement = parseStatement(parent);
                if (token.getType() == TokenType.ELSE) {
                    consume();
                    IStatement falseStatement = parseStatement(parent);
                    statement = new IfStatement(ifToken.getBeginLocation(), condition, trueStatement, falseStatement);
                } else {
                    statement = new IfStatement(ifToken.getBeginLocation(), condition, trueStatement);
                }
            }
            case WHILE -> {
                Token whileToken = token;
                consume();
                Expression condition = parseExpression(parent);
                IStatement repeatStatement = parseStatement(parent);
                statement = new WhileStatement(whileToken.getBeginLocation(), condition, repeatStatement);
            }
            case RETURN -> {
                Token returnToken = token;
                consume();
                if (token.getType() == TokenType.SEMICOLON) {
                    statement = new ReturnStatement(returnToken.getBeginLocation(), null);
                } else {
                    statement = new ReturnStatement(returnToken.getBeginLocation(), parseExpression(parent));
                }
            }
            default -> {
                Token first = token;
                Token second = consume();
                // ExpressionStatement | VariableDeclarationStatement | VariableInitializationStatement
                if (first.getType().isPrimitive()) {
                    // Variable declaration or initialization
                    consume();
                    statement = parseVariableDeclaration(parent, first, second);
                } else {
                    if (second.getType() == TokenType.IDENTIFIER) {
                        // Variable declaration or initialization
                        consume();
                        statement = parseVariableDeclaration(parent, first, second);
                    } else {
                        // Expression
                        statement = new ExpressionStatement(first.getBeginLocation(), parseExpression(parent, first));
                    }
                }
            }
        }
        return statement;
    }

    private VariableDeclarationStatement parseVariableDeclaration(MemberDeclaration parent, Token typeToken, Token identifierToken) throws ParseException {
        Symbol type = new Symbol(parent, typeToken.getText());
        Symbol identifier = new Symbol(parent, identifierToken.getText());

        if (token.getType() == TokenType.SEMICOLON) {
            VariableDeclarationStatement declaration = new VariableDeclarationStatement(typeToken.getBeginLocation(), type, identifier);
            expectAndConsume(TokenType.SEMICOLON);
            return declaration;
        } else if (token.getType() == TokenType.ASSIGN) {
            consume();
            VariableDeclarationStatement declaration = new VariableInitializationStatement(typeToken.getBeginLocation(), type, identifier, parseExpression(parent));
            expectAndConsume(TokenType.SEMICOLON);
            return declaration;
        } else {
            throw new ParseException(token.getBeginLocation(), "Expected semicolon or assignment");
        }
    }

    private Expression parseExpression(MemberDeclaration parent) throws ParseException {
        Token old = token;
        consume();
        return parseExpression(parent, old);
    }

    private Expression parseExpression(MemberDeclaration parent, Token first) throws ParseException {
        Expression result = parsePrimaryExpression(parent, first);
        Token operation = token;

        if (!operation.getType().isBinaryOperator()) {
            return result;
        }

        consume();
        Expression rightExpression = parseExpression(parent);

        switch (operation.getType()) {
            case PERIOD -> {
                result = new VariableAccessExpression(result.location(), result, rightExpression);
            }
            case MUL -> {
                result = new MultiplyExpression(result.location(), result, rightExpression);
            }
            case DIV -> {
                result = new DivideExpression(result.location(), result, rightExpression);
            }
            case MOD -> {
                result = new ModuloExpression(result.location(), result, rightExpression);
            }
            case SHIFT_LEFT -> {
                result = new ShiftLeftExpression(result.location(), result, rightExpression);
            }
            case SHIFT_RIGHT -> {
                result = new ShiftRightExpression(result.location(), result, rightExpression);
            }
            case SHIFT_RIGHT_LOGICAL -> {
                result = new ShiftRightLogicalExpression(result.location(), result, rightExpression);
            }
            case LESS_THAN -> {
                result = new LessThanExpression(result.location(), result, rightExpression);
            }
            case LESS_EQUAL -> {
                result = new LessThanOrEqualsExpression(result.location(), result, rightExpression);
            }
            case GREATER_THAN -> {
                result = new GreaterThanExpression(result.location(), result, rightExpression);
            }
            case GREATER_EQUAL -> {
                result = new GreaterThanOrEqualsExpression(result.location(), result, rightExpression);
            }
            case EQUALS -> {
                result = new EqualsExpression(result.location(), result, rightExpression);
            }
            case NOT_EQUALS -> {
                result = new NotEqualsExpression(result.location(), result, rightExpression);
            }
            case BAND -> {
                result = new BinaryAndExpression(result.location(), result, rightExpression);
            }
            case XOR -> {
                result = new BinaryXorExpression(result.location(), result, rightExpression);
            }
            case BOR -> {
                result = new BinaryOrExpression(result.location(), result, rightExpression);
            }
            case LAND -> {
                result = new LogicalAndExpression(result.location(), result, rightExpression);
            }
            case LOR -> {
                result = new LogicalOrExpression(result.location(), result, rightExpression);
            }
            case ASSIGN -> {
                result = new AssignmentExpression(result.location(), result, rightExpression);
            }
        }

        return result;
    }

    private Expression parsePrimaryExpression(MemberDeclaration parent) throws ParseException {
        Token old = token;
        consume();
        return parsePrimaryExpression(parent, old);
    }

    private Expression parsePrimaryExpression(MemberDeclaration parent, Token first) throws ParseException {
        Expression expression;
        switch (first.getType()) {
            case LNOT -> {
                return new LogicalInvertExpression(first.getBeginLocation(), parseExpression(parent));
            }
            case BNOT -> {
                return new BinaryInvertExpression(first.getBeginLocation(), parseExpression(parent));
            }
            case LPAREN -> {
                expression = new WrappedExpression(parseExpression(parent));
                expectAndConsume(TokenType.RPAREN);
            }
            case NEW -> {
                Token typeToken = expectAndConsume(TokenType.IDENTIFIER);
                expectAndConsume(TokenType.LPAREN);
                List<Expression> parameters = new ArrayList<>();
                if (token.getType() != TokenType.RPAREN) {
                    parameters.add(parseExpression(parent));
                    while (token.getType() == TokenType.COMMA) {
                        consume();
                        parameters.add(parseExpression(parent));
                    }
                }
                expectAndConsume(TokenType.RPAREN);
                expression = new ObjectInstantiationExpression(first.getBeginLocation(), new Symbol(parent, typeToken.getText()), parameters);
            }
            case CHAR_IMMEDIATE -> {
                expression = new CharExpression(first.getBeginLocation(), first.getText().substring(1, first.getText().length() - 1));
            }
            case STRING_IMMEDIATE -> {
                expression = new StringExpression(first.getBeginLocation(), first.getText().substring(1, first.getText().length() - 1));
            }
            case FLOAT_IMMEDIATE -> {
                expression = new FloatExpression(first.getBeginLocation(), first.getText());
            }
            case INTEGER_IMMEDIATE -> {
                expression = new IntegerExpression(first.getBeginLocation(), first.getText());
            }
            case IDENTIFIER -> {
                if (first.getType() == TokenType.LPAREN) {
                    List<Expression> parameters = new ArrayList<>();
                    parameters.add(parseExpression(parent));
                    while (token.getType() == TokenType.COMMA) {
                        consume();
                        parameters.add(parseExpression(parent));
                    }
                    expectAndConsume(TokenType.RPAREN);
                    expression = new MethodInvocationExpression(first.getBeginLocation(), new Symbol(parent, first.getText()), parameters);
                } else {
                    expression = new VariableExpression(first.getBeginLocation(), new Symbol(parent, first.getText()));
                }
            }
            default -> throw new ParseException(first.getBeginLocation(), String.format("Unexpected start of expression '%s'", first.getText()));
        }
        return expression;
    }

    private Access consumeAccessIfPresent() throws ParseException {
        switch (token.getType()) {
            case PUBLIC -> {
                consume();
                return Access.PUBLIC;
            }
            case PROTECTED -> {
                consume();
                return Access.PROTECTED;
            }
            case PRIVATE -> {
                consume();
                return Access.PRIVATE;
            }
            default -> {
                return Access.PRIVATE;
            }
        }
    }

    private boolean consumeStaticIfPresent() throws ParseException{
        if (token.getType() == TokenType.STATIC) {
            consume();
            return true;
        } else {
            return false;
        }
    }

    private boolean consumeFinalIfPresent() throws ParseException{
        if (token.getType() == TokenType.FINAL) {
            consume();
            return true;
        } else {
            return false;
        }
    }

    private void expect(TokenType type) throws ParseException {
        if (token.getType() != type) {
            throw new ParseException(token.getBeginLocation(), type, token);
        }
    }

    private Token expectAndConsume(TokenType type) throws ParseException {
        if (token.getType() != type) {
            throw new ParseException(token.getBeginLocation(), type, token);
        }
        Token old = token;
        consume();
        return old;
    }

    private Token expectAndConsumeMaybeEof(TokenType type) throws ParseException {
        if (token.getType() != type) {
            throw new ParseException(token.getBeginLocation(), type, token);
        }
        Token old = token;
        consumeMaybeEof();
        return old;
    }

    private Token consume() throws ParseException{
        token = lexer.getNextToken();
        if (token.getType() == TokenType.EOF) {
            throw new ParseException(token.getEndLocation(), "Unexpected EOF");
        }
        return token;
    }

    private Token consumeMaybeEof() {
        token = lexer.getNextToken();
        return token;
    }

}
