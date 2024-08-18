package pro.trevor.joe.parser;

import pro.trevor.joe.lexer.Lexer;
import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.lexer.Token;
import pro.trevor.joe.lexer.TokenType;
import pro.trevor.joe.tree.IStatement;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.declaration.*;
import pro.trevor.joe.tree.expression.*;
import pro.trevor.joe.tree.expression.binary.*;
import pro.trevor.joe.tree.expression.literal.CharExpression;
import pro.trevor.joe.tree.expression.literal.FloatExpression;
import pro.trevor.joe.tree.expression.literal.IntegerExpression;
import pro.trevor.joe.tree.expression.literal.StringExpression;
import pro.trevor.joe.tree.expression.unary.BinaryInvertExpression;
import pro.trevor.joe.tree.expression.unary.LogicalInvertExpression;
import pro.trevor.joe.tree.statement.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final Lexer lexer;
    private final List<ParseException> errors;

    private Token token;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        consumeMaybeEof();
    }


    /*
     * [public|private|protected] [static] [final] class|enum|interface $identifier { MemberDeclaration* }
     */
    public ClassDeclaration parseClass() throws ParseException {

        Access access = consumeAccessIfPresent();
        boolean isStatic = consumeStaticIfPresent();
        boolean isFinal = consumeFinalIfPresent();

        return parseClass(access, isStatic, isFinal);
    }

    /*
     * class|enum|interface $identifier { MemberDeclaration* }
     */
    private ClassDeclaration parseClass(Access access, boolean isStatic, boolean isFinal) throws ParseException {
        Token classToken = expectAndConsume(TokenType.CLASS);
        Token classNameToken = expectAndConsume(TokenType.IDENTIFIER);

        ClassDeclaration classDeclaration = new ClassDeclaration(classToken.getBeginLocation(), new Symbol(classNameToken.getText()), access, isStatic, isFinal);

        expectAndConsume(TokenType.LBRACE);

        while (token.getType() != TokenType.RBRACE) {
            classDeclaration.addMemberDeclaration((ClassMember) parseTypeMemberDeclaration());
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
     *     class|enum|interface $identifier { MemberDeclaration* } |
     *     fn $identifier([ParameterDeclaration (, ParameterDeclaration)*]) $type { Statement* } |
     *     $type $identifier;
     *   )
     * *)
     *
     */
    private TypeMember parseTypeMemberDeclaration() throws ParseException {
        Access access = consumeAccessIfPresent();
        boolean isStatic = consumeStaticIfPresent();
        boolean isFinal = consumeFinalIfPresent();

        TypeMember declaration;

        if (token.getType() == TokenType.CLASS || token.getType() == TokenType.ENUM || token.getType() == TokenType.INTERFACE) {
            // Inner class declaration
            declaration = parseClass(access, isStatic, isFinal);
        } else if (token.getType() == TokenType.FN) {
            // Member function declaration
            Location beginning = token.getBeginLocation();
            consume();
            Token identifierToken = expectAndConsume(TokenType.IDENTIFIER);

            FunctionDeclaration functionDeclaration = new FunctionDeclaration(beginning, new Symbol(identifierToken.getText()), access, isStatic, isFinal, null, new ArrayList<>(), null);

            expectAndConsume(TokenType.LPAREN);
            if (token.getType() != TokenType.RPAREN) {
                do {
                    Token parameterType = token;
                    Token parameterIdentifier = expectAndConsume(TokenType.IDENTIFIER);
                    functionDeclaration.getArguments().add(new ParameterDeclaration(parameterType.getBeginLocation(), new Symbol(parameterType.getText()), new Symbol(parameterIdentifier.getText())));
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
                code.addStatement(parseStatement());
            }
            expectAndConsume(TokenType.RBRACE);

            functionDeclaration.setReturnType(new Symbol(typeToken.getText()));
            functionDeclaration.setCode(code);
            declaration = functionDeclaration;
        } else if (token.getType() == TokenType.IDENTIFIER || token.getType().isPrimitive()){
            // Member variable declaration
            Token typeToken = token;
            Token identifierToken = consume();
            consume();
            expectAndConsume(TokenType.SEMICOLON);
            declaration = new VariableDeclaration(typeToken.getBeginLocation(), new Symbol(identifierToken.getText()), access, isStatic, isFinal, new Symbol(typeToken.getText()));
        } else {
            throw new ParseException(token.getBeginLocation(), new TokenType[]{TokenType.CLASS, TokenType.FN, TokenType.IDENTIFIER}, token);
        }

        return declaration;
    }

    private Block parseCodeBlock() throws ParseException {
        Block block = new Block(token.getBeginLocation());

        expectAndConsume(TokenType.LBRACE);
        while (token.getType() != TokenType.RBRACE) {
            block.addStatement(parseStatement());
        }
        expectAndConsume(TokenType.RBRACE);

        return block;
    }

    private IStatement parseStatement() throws ParseException {
        IStatement statement;
        switch (token.getType()) {
            case LBRACE -> {
                statement = parseCodeBlock();
            }
            case SEMICOLON -> {
                statement = new EmptyStatement(token.getBeginLocation());
                consume();
            }
            case IF -> {
                Token ifToken = token;
                consume();
                expectAndConsume(TokenType.LPAREN);
                Expression condition = parseExpression();
                expectAndConsume(TokenType.RPAREN);
                IStatement trueStatement = parseStatement();
                if (token.getType() == TokenType.ELSE) {
                    consume();
                    IStatement falseStatement = parseStatement();
                    statement = new IfStatement(ifToken.getBeginLocation(), condition, trueStatement, falseStatement);
                } else {
                    statement = new IfStatement(ifToken.getBeginLocation(), condition, trueStatement);
                }
            }
            case WHILE -> {
                Token whileToken = token;
                consume();
                Expression condition = parseExpression();
                IStatement repeatStatement = parseStatement();
                statement = new WhileStatement(whileToken.getBeginLocation(), condition, repeatStatement);
            }
            case RETURN -> {
                Token returnToken = token;
                consume();
                if (token.getType() == TokenType.SEMICOLON) {
                    statement = new ReturnStatement(returnToken.getBeginLocation(), null);
                } else {
                    statement = new ReturnStatement(returnToken.getBeginLocation(), parseExpression());
                }
            }
            default -> {
                Token first = token;
                Token second = consume();
                // ExpressionStatement | VariableDeclarationStatement | VariableInitializationStatement
                if (first.getType().isPrimitive()) {
                    // Variable declaration or initialization
                    consume();
                    statement = parseVariableDeclaration(first, second);
                } else {
                    if (second.getType() == TokenType.IDENTIFIER) {
                        // Variable declaration or initialization
                        consume();
                        statement = parseVariableDeclaration(first, second);
                    } else {
                        // Expression
                        statement = new ExpressionStatement(first.getBeginLocation(), parseExpression(first));
                        expectAndConsume(TokenType.SEMICOLON);
                    }
                }
            }
        }
        return statement;
    }

    private VariableDeclarationStatement parseVariableDeclaration(Token typeToken, Token identifierToken) throws ParseException {
        Symbol type = new Symbol(typeToken.getText());
        Symbol identifier = new Symbol(identifierToken.getText());

        if (token.getType() == TokenType.SEMICOLON) {
            VariableDeclarationStatement declaration = new VariableDeclarationStatement(typeToken.getBeginLocation(), type, identifier);
            expectAndConsume(TokenType.SEMICOLON);
            return declaration;
        } else if (token.getType() == TokenType.ASSIGN) {
            consume();
            VariableDeclarationStatement declaration = new VariableInitializationStatement(typeToken.getBeginLocation(), type, identifier, parseExpression());
            expectAndConsume(TokenType.SEMICOLON);
            return declaration;
        } else {
            throw new ParseException(token.getBeginLocation(), "Expected semicolon or assignment");
        }
    }

    private Expression parseExpression() throws ParseException {
        Token old = token;
        consume();
        return parseExpression(old, Integer.MIN_VALUE);
    }

    private Expression parseExpression(int precedence) throws ParseException {
        Token old = token;
        consume();
        return parseExpression(old, precedence);
    }

    private Expression parseExpression(Token first) throws ParseException {
        return parseExpression(first, Integer.MIN_VALUE);
    }

    private Expression parseExpression(Token first, int precedence) throws ParseException {
        Expression output = parsePrimaryExpression(first);
        Token operation = token;

        while (operation.getType().isBinaryOperator() || operation.getType().isPostfixOperator()) {
            if (operation.getType().isBinaryOperator()) {
                int tokenPrecedence = precedence(token.getType());
                boolean lowerPrecedence = tokenPrecedence < precedence || (associativity(token.getType()) == Associativity.LEFT_TO_RIGHT && tokenPrecedence == precedence);
                if (lowerPrecedence) {
                    break;
                }
                consume();
                Expression rightExpression = parseExpression(precedence(operation.getType()));

                switch (operation.getType()) {
                    case PERIOD -> {
                        output = new VariableAccessExpression(output.location(), output, rightExpression);
                    }
                    case MUL -> {
                        output = new MultiplyExpression(output.location(), output, rightExpression);
                    }
                    case DIV -> {
                        output = new DivideExpression(output.location(), output, rightExpression);
                    }
                    case MOD -> {
                        output = new ModuloExpression(output.location(), output, rightExpression);
                    }
                    case ADD -> {
                        output = new AdditionExpression(output.location(), output, rightExpression);
                    }
                    case SUB -> {
                        output = new SubtractionExpression(output.location(), output, rightExpression);
                    }
                    case SHIFT_LEFT -> {
                        output = new ShiftLeftExpression(output.location(), output, rightExpression);
                    }
                    case SHIFT_RIGHT -> {
                        output = new ShiftRightExpression(output.location(), output, rightExpression);
                    }
                    case SHIFT_RIGHT_LOGICAL -> {
                        output = new ShiftRightLogicalExpression(output.location(), output, rightExpression);
                    }
                    case LESS_THAN -> {
                        output = new LessThanExpression(output.location(), output, rightExpression);
                    }
                    case LESS_EQUAL -> {
                        output = new LessThanOrEqualsExpression(output.location(), output, rightExpression);
                    }
                    case GREATER_THAN -> {
                        output = new GreaterThanExpression(output.location(), output, rightExpression);
                    }
                    case GREATER_EQUAL -> {
                        output = new GreaterThanOrEqualsExpression(output.location(), output, rightExpression);
                    }
                    case EQUALS -> {
                        output = new EqualsExpression(output.location(), output, rightExpression);
                    }
                    case NOT_EQUALS -> {
                        output = new NotEqualsExpression(output.location(), output, rightExpression);
                    }
                    case BAND -> {
                        output = new BinaryAndExpression(output.location(), output, rightExpression);
                    }
                    case XOR -> {
                        output = new BinaryXorExpression(output.location(), output, rightExpression);
                    }
                    case BOR -> {
                        output = new BinaryOrExpression(output.location(), output, rightExpression);
                    }
                    case LAND -> {
                        output = new LogicalAndExpression(output.location(), output, rightExpression);
                    }
                    case LOR -> {
                        output = new LogicalOrExpression(output.location(), output, rightExpression);
                    }
                    case ASSIGN -> {
                        output = new AssignmentExpression(output.location(), output, rightExpression);
                    }
                    default -> throw new ParseException(operation.getBeginLocation(), "Unhandled binary operator " + operation.getText());
                }
            } else if (operation.getType().isPostfixOperator()) {
                consume();
                switch (operation.getType()) {
                    case LPAREN -> {
                        List<Expression> parameters = new ArrayList<>();
                        if (token.getType() != TokenType.RPAREN) {
                            parameters.add(parseExpression());
                            while (token.getType() == TokenType.COMMA) {
                                consume();
                                parameters.add(parseExpression());
                            }
                        }
                        expectAndConsume(TokenType.RPAREN);
                        output = new MethodInvocationExpression(first.getBeginLocation(), output, parameters);
                    }
                    case LBRACKET -> {
                        output = new ArrayIndexExpression(operation.getBeginLocation(), output, parseExpression());
                        expectAndConsume(TokenType.RBRACKET);
                    }
                    default ->
                            throw new ParseException(operation.getBeginLocation(), "Unhandled postfix operator " + operation.getText());
                }
            } else {
                throw new ParseException(token.getBeginLocation(), "Unexpected internal state");
            }
            operation = token;
        }

        return output;
    }

    private Expression parsePrimaryExpression(Token first) throws ParseException {
        Expression expression;
        switch (first.getType()) {
            case LNOT -> {
                return new LogicalInvertExpression(first.getBeginLocation(), parseExpression());
            }
            case BNOT -> {
                return new BinaryInvertExpression(first.getBeginLocation(), parseExpression());
            }
            case LPAREN -> {
                expression = new WrappedExpression(parseExpression());
                expectAndConsume(TokenType.RPAREN);
            }
            case NEW -> {
                Token typeToken = expectAndConsume(TokenType.IDENTIFIER);
                expectAndConsume(TokenType.LPAREN);
                List<Expression> parameters = new ArrayList<>();
                if (token.getType() != TokenType.RPAREN) {
                    parameters.add(parseExpression());
                    while (token.getType() == TokenType.COMMA) {
                        consume();
                        parameters.add(parseExpression());
                    }
                }
                expectAndConsume(TokenType.RPAREN);
                expression = new ObjectInstantiationExpression(first.getBeginLocation(), new Symbol(typeToken.getText()), parameters);
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
                expression = new VariableExpression(first.getBeginLocation(), new Symbol(first.getText()));
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

    private static int precedence(TokenType tokenType) {
        return switch (tokenType) {
            case ASSIGN -> 1;
            case LOR -> 2;
            case LAND -> 3;
            case BOR -> 4;
            case XOR -> 5;
            case BAND -> 6;
            case EQUALS, NOT_EQUALS -> 7;
            case LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL -> 8;
            case SHIFT_LEFT, SHIFT_RIGHT, SHIFT_RIGHT_LOGICAL -> 9;
            case ADD, SUB -> 10;
            case MUL, DIV, MOD -> 11;
            case PERIOD -> 12;
            default -> -1;
        };
    }

    private static Associativity associativity(TokenType tokenType) {
        return switch (tokenType) {
            case ASSIGN -> Associativity.RIGHT_TO_LEFT;
            default -> Associativity.LEFT_TO_RIGHT;
        };
    }

}
