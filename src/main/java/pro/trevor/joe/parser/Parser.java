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
import pro.trevor.joe.tree.expression.literal.CharExpression;
import pro.trevor.joe.tree.expression.literal.FloatExpression;
import pro.trevor.joe.tree.expression.literal.IntegerExpression;
import pro.trevor.joe.tree.expression.literal.StringExpression;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;
import pro.trevor.joe.tree.expression.unary.BinaryInvertExpression;
import pro.trevor.joe.tree.expression.unary.LogicalInvertExpression;
import pro.trevor.joe.tree.statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

            FunctionDeclaration functionDeclaration = new FunctionDeclaration(beginning, new Symbol(parent, identifierToken.getText()), parent, isStatic, isFinal, new ArrayList<>(), null, null);

            expectAndConsume(TokenType.LPAREN);
            int number = 0;
            if (token.getType() != TokenType.RPAREN) {
                do {
                    Token parameterType = token;
                    Token parameterIdentifier = expectAndConsume(TokenType.IDENTIFIER);
                    Optional<Type> optionalParameterReturnType = ReturnType.typeFromToken(token, functionDeclaration);
                    if (optionalParameterReturnType.isEmpty()) {
                        throw new ParseException(parameterType.getBeginLocation(), "Parameter type must be primitive or an identifier");
                    }
                    functionDeclaration.getArguments().add(new ParameterDeclaration(parameterType.getBeginLocation(), optionalParameterReturnType.get(), new Symbol(functionDeclaration, parameterIdentifier.getText()), functionDeclaration, number++));
                    if (token.getType() == TokenType.COMMA) {
                        consume();
                    }
                }
                while (token.getType() != TokenType.RPAREN);
            }
            expectAndConsume(TokenType.RPAREN);

            Token typeToken = token;
            Optional<Type> optionalReturnType = ReturnType.typeFromToken(token, parent);
            if (optionalReturnType.isEmpty()) {
                throw new ParseException(typeToken.getBeginLocation(), "Function type must be primitive or an identifier");
            }

            Block code = new Block(token.getBeginLocation());

            consume();
            expectAndConsume(TokenType.LBRACE);
            int depth = 1;
            while (depth > 0) {
                if (token.getType() == TokenType.RBRACE) {
                    --depth;
                } else if (token.getType() == TokenType.LBRACE) {
                    ++depth;
                }
//                code.addStatement(parseStatement(functionDeclaration));
                consume();
            }

            functionDeclaration.setType(optionalReturnType.get());
            functionDeclaration.setCode(code);
            declaration = functionDeclaration;
        } else if (token.getType() == TokenType.IDENTIFIER || token.getType().isPrimitive()){
            // Member variable declaration
            Token typeToken = token;
            Token identifierToken = consume();
            expectAndConsume(TokenType.SEMICOLON);
            Optional<Type> optionalReturnType = ReturnType.typeFromToken(token, parent);
            if (optionalReturnType.isEmpty()) {
                throw new ParseException(typeToken.getBeginLocation(), "Member variable type must be primitive or an identifier");
            }
            Type returnType = optionalReturnType.get();
            declaration = new VariableDeclaration(typeToken.getBeginLocation(), returnType, new Symbol(parent, identifierToken.getText()), parent, access, isStatic, isFinal);
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
                Expression condition = parseExpression(parent);
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
        Type type = typeFromToken(parent, typeToken);
        Symbol identifier = new Symbol(parent, identifierToken.getText());

        if (token.getType() == TokenType.SEMICOLON) {
            consume();
            return new VariableDeclarationStatement(typeToken.getBeginLocation(), type, identifier);
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
        return parseExpression(parent, consume());
    }

    private Expression parseExpression(MemberDeclaration parent, Token first) throws ParseException {
        Expression result = parsePrimaryExpression(parent, first);

        switch (token.getType()) {
            case PERIOD -> {

            }
            case MUL -> {

            }
            case DIV -> {

            }
            case MOD -> {

            }
            case SHIFT_LEFT -> {

            }
            case SHIFT_RIGHT -> {

            }
            case SHIFT_RIGHT_LOGICAL -> {

            }
            case LESS_THAN -> {

            }
            case LESS_EQUAL -> {

            }
            case GREATER_THAN -> {

            }
            case GREATER_EQUAL -> {

            }
            case EQUALS -> {

            }
            case NOT_EQUALS -> {

            }
            case BAND -> {

            }
            case XOR -> {

            }
            case BOR -> {

            }
            case LAND -> {

            }
            case LOR -> {

            }
            case ASSIGN -> {

            }
        }

        return result;
    }

    private Expression parsePrimaryExpression(MemberDeclaration parent) throws ParseException {
        return parsePrimaryExpression(parent, consume());
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

    private Type typeFromToken(Declaration declaration, Token token) throws ParseException {
        Optional<Type> optionalType = ReturnType.typeFromToken(token, declaration);
        if (optionalType.isEmpty()) {
            throw new ParseException(token.getBeginLocation(), "Expected primitive or identifier");
        } else {
            return optionalType.get();
        }
    }

}
