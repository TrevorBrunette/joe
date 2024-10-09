package pro.trevor.joe.parser;

import pro.trevor.joe.lexer.Lexer;
import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.lexer.Token;
import pro.trevor.joe.lexer.TokenType;
import pro.trevor.joe.parser.tree.IStatement;
import pro.trevor.joe.parser.tree.Symbol;
import pro.trevor.joe.parser.tree.Type;
import pro.trevor.joe.parser.tree.declaration.*;
import pro.trevor.joe.parser.tree.expression.*;
import pro.trevor.joe.parser.tree.expression.binary.*;
import pro.trevor.joe.parser.tree.expression.literal.CharExpression;
import pro.trevor.joe.parser.tree.expression.literal.FloatExpression;
import pro.trevor.joe.parser.tree.expression.literal.IntegerExpression;
import pro.trevor.joe.parser.tree.expression.literal.StringExpression;
import pro.trevor.joe.parser.tree.expression.unary.BinaryInvertExpression;
import pro.trevor.joe.parser.tree.expression.unary.LogicalInvertExpression;
import pro.trevor.joe.parser.tree.statement.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final Lexer lexer;
    private final List<ParseException> errors;
    private final List<TopLevelDeclaration> file;

    private Token token;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        this.file = new ArrayList<>();
        consumeMaybeEof();
    }

    /*
     * (
     *   ([public|private|protected] [static] [final] class|enum|interface $identifier { ...* }) |
     *   (fn $identifier([type $identifier (, type $identifier)*]) { Statement* }) |
     *   (extern fn $identifier([type $identifier (, type $identifier)*]);)
     * )*
     */
    public List<TopLevelDeclaration> parseFile() throws ParseException {
        while (token.getType() != TokenType.EOF) {
            file.add(parseTopLevelDeclaration());
        }
        return file;
    }

    /*
     * [public|private|protected] [static] [final] class|enum|interface|fn $identifier { ...* }
     */
    private TopLevelDeclaration parseTopLevelDeclaration() throws ParseException {

        Access access = consumeAccessIfPresent(Access.PRIVATE);
        boolean isStatic = consumeStaticIfPresent();
        boolean isFinal = consumeFinalIfPresent();
        boolean isExtern = consumeExternIfPresent();

        switch (token.getType()) {
            case CLASS, ENUM, INTERFACE -> {
                if (isExtern) {
                    throw new ParseException(token.getBeginLocation(), "Type cannot be declared as extern");
                }
                return parseType(access, isStatic, isFinal);
            }
            case FN -> {
                if (isExtern) {
                    return parseFunctionStubDeclaration(access, isStatic, isFinal);
                } else {
                    return parseFunctionDeclaration(access, isStatic, isFinal);
                }
            }
            default -> throw new ParseException(token.getBeginLocation(), new TokenType[]{TokenType.CLASS, TokenType.ENUM, TokenType.INTERFACE, TokenType.FN}, token);
        }
    }

    /*
     * class|enum|interface $identifier { ...* }
     */
    private TypeDeclaration parseType(Access access, boolean isStatic, boolean isFinal) throws ParseException {
        return switch (token.getType()) {
            case CLASS -> parseClass(access, isStatic, isFinal);
            case ENUM -> parseEnum(access, isStatic, isFinal);
            case INTERFACE -> parseInterface(access, isStatic, isFinal);
            default -> throw new ParseException(token.getBeginLocation(), new TokenType[]{TokenType.CLASS, TokenType.ENUM, TokenType.INTERFACE}, token);
        };
    }

    /*
     * class $identifier { MemberDeclaration* }
     */
    private ClassDeclaration parseClass(Access access, boolean isStatic, boolean isFinal) throws ParseException {
        Token classToken = expectAndConsume(TokenType.CLASS);
        Token classNameToken = expectAndConsume(TokenType.IDENTIFIER);

        ClassDeclaration classDeclaration = new ClassDeclaration(classToken.getBeginLocation(), new Symbol(classNameToken.getText()), access, isStatic, isFinal);

        expectAndConsume(TokenType.LBRACE);

        while (token.getType() != TokenType.RBRACE) {
            classDeclaration.addMemberDeclaration(parseClassMember());
        }

        expectAndConsumeMaybeEof(TokenType.RBRACE);
        return classDeclaration;
    }

    /*
     * interface $identifier { InterfaceMember* }
     */
    private InterfaceDeclaration parseInterface(Access access, boolean isStatic, boolean isFinal) throws ParseException {
        Token interfaceToken = expectAndConsume(TokenType.INTERFACE);
        Token interfaceNameToken = expectAndConsume(TokenType.IDENTIFIER);

        InterfaceDeclaration interfaceDeclaration = new InterfaceDeclaration(interfaceToken.getBeginLocation(), new Symbol(interfaceNameToken.getText()), access, isStatic, isFinal);

        expectAndConsume(TokenType.LBRACE);

        while (token.getType() != TokenType.RBRACE) {
            interfaceDeclaration.addInterfaceMember(parseInterfaceMember());
        }

        expectAndConsumeMaybeEof(TokenType.RBRACE);
        return interfaceDeclaration;
    }

    /*
     * enum $identifier { EnumMember* }
     */
    private EnumDeclaration parseEnum(Access access, boolean isStatic, boolean isFinal) throws ParseException {
        Token interfaceToken = expectAndConsume(TokenType.ENUM);
        Token interfaceNameToken = expectAndConsume(TokenType.IDENTIFIER);

        EnumDeclaration enumDeclaration = new EnumDeclaration(interfaceToken.getBeginLocation(), new Symbol(interfaceNameToken.getText()), access, isStatic, isFinal);

        expectAndConsume(TokenType.LBRACE);

        if (token.getType() != TokenType.RBRACE) {
            enumDeclaration.addEnumMember(parseEnumMember());
            while (token.getType() == TokenType.COMMA) {
                expectAndConsume(TokenType.COMMA);
                enumDeclaration.addEnumMember(parseEnumMember());
            }
        }

        expectAndConsumeMaybeEof(TokenType.RBRACE);
        return enumDeclaration;
    }

    /*
     * (
     *   [public|private|protected]
     *   [static]
     *   [final]
     *   (
     *     class $identifier { ClassMember* } |
     *     enum $identifier { EnumMember* } |
     *     interface $identifier { InterfaceMember* } |
     *     fn $identifier([ParameterDeclaration (, ParameterDeclaration)*]) $type { Statement* } |
     *     $type $identifier;
     *   )
     * )
     *
     */
    private ClassMember parseClassMember() throws ParseException {
        Access access = consumeAccessIfPresent(Access.PRIVATE);
        boolean isStatic = consumeStaticIfPresent();
        boolean isFinal = consumeFinalIfPresent();

        ClassMember declaration;

        if (token.getType() == TokenType.CLASS || token.getType() == TokenType.ENUM || token.getType() == TokenType.INTERFACE) {
            declaration = (ClassMember) parseType(access, isStatic, isFinal);
        } else if (token.getType() == TokenType.FN) {
            declaration = parseFunctionDeclaration(access, isStatic, isFinal);
        } else if (token.getType() == TokenType.IDENTIFIER || token.getType().isPrimitive()){
            // Member variable declaration
            Token begin = token;
            Type type = parseType();
            Token identifierToken = expectAndConsume(TokenType.IDENTIFIER);
            expectAndConsume(TokenType.SEMICOLON);
            declaration = new VariableDeclaration(begin.getBeginLocation(), new Symbol(identifierToken.getText()), access, isStatic, isFinal, type);
        } else {
            throw new ParseException(token.getBeginLocation(), new TokenType[]{TokenType.CLASS, TokenType.ENUM, TokenType.INTERFACE, TokenType.FN, TokenType.IDENTIFIER}, token);
        }

        return declaration;
    }

    /*
     * (
     *   [public|private|protected]
     *   [static]
     *   [final]
     *   (
     *     class $identifier { ClassMember* } |
     *     enum $identifier { EnumMember* } |
     *     interface $identifier { InterfaceMember* } |
     *     fn $identifier([ParameterDeclaration (, ParameterDeclaration)*]) $type;
     *   )
     * )
     *
     */
    private InterfaceMember parseInterfaceMember() throws ParseException {
        Access access = consumeAccessIfPresent(Access.PUBLIC);
        boolean isStatic = consumeStaticIfPresent();
        boolean isFinal = consumeFinalIfPresent();

        InterfaceMember declaration;

        if (token.getType() == TokenType.CLASS || token.getType() == TokenType.ENUM || token.getType() == TokenType.INTERFACE) {
            declaration = (InterfaceMember) parseType(access, isStatic, isFinal);
        } else if (token.getType() == TokenType.FN) {
            declaration = parseFunctionStubDeclaration(access, isStatic, isFinal);
        } else {
            throw new ParseException(token.getBeginLocation(), new TokenType[]{TokenType.CLASS, TokenType.ENUM, TokenType.INTERFACE, TokenType.FN}, token);
        }

        return declaration;
    }

    /*
     *   $identifier[([Type] (, Type)*])]*
     */
    private EnumMember parseEnumMember() throws ParseException {
        EnumMember declaration;

        if (token.getType() == TokenType.IDENTIFIER) {
            Token identifier = expectAndConsume(TokenType.IDENTIFIER);
            List<Type> types = new ArrayList<>();
            if (token.getType() == TokenType.LPAREN) {
                expectAndConsume(TokenType.LPAREN);
                if (token.getType() != TokenType.RPAREN) {
                    Type type = parseType();
                    types.add(type);

                    while (token.getType() == TokenType.COMMA) {
                        expectAndConsume(TokenType.COMMA);
                        type = parseType();
                        types.add(type);
                    }
                }
                expectAndConsume(TokenType.RPAREN);
            }
            declaration = new EnumVariantDeclaration(identifier.getBeginLocation(), new Symbol(identifier.getText()), types);
        } else {
            throw new ParseException(token.getBeginLocation(), TokenType.IDENTIFIER, token);
        }

        return declaration;
    }


    private List<ParameterDeclaration> parseParameterDeclarations() throws ParseException{
        List<ParameterDeclaration> declarations = new ArrayList<>();
        expectAndConsume(TokenType.LPAREN);

        if (token.getType() != TokenType.RPAREN) {
            declarations.add(parseParameterDeclaration());
            while (token.getType() == TokenType.COMMA) {
                expectAndConsume(TokenType.COMMA);
                declarations.add(parseParameterDeclaration());
            }
        }

        expectAndConsume(TokenType.RPAREN);
        return declarations;
    }

    private ParameterDeclaration parseParameterDeclaration() throws ParseException {
        Token begin = token;
        Type type = parseType();
        Token identifier = expectAndConsume(TokenType.IDENTIFIER);

        return new ParameterDeclaration(begin.getBeginLocation(), type, new Symbol(identifier.getText()));
    }

    private FunctionStubDeclaration parseFunctionStubDeclaration(Access access, boolean isStatic, boolean isFinal) throws ParseException {
        Location beginning = token.getBeginLocation();
        consume();
        Token identifierToken = expectAndConsume(TokenType.IDENTIFIER);

        List<ParameterDeclaration> parameters = parseParameterDeclarations();

        Type type = parseType();
        expectAndConsumeMaybeEof(TokenType.SEMICOLON);

        return new FunctionStubDeclaration(beginning, new Symbol(identifierToken.getText()), access, isStatic, isFinal, type, parameters);
    }

    private FunctionDeclaration parseFunctionDeclaration(Access access, boolean isStatic, boolean isFinal) throws ParseException {
        Location beginning = token.getBeginLocation();
        consume();
        Token identifierToken = expectAndConsume(TokenType.IDENTIFIER);

        List<ParameterDeclaration> parameters = parseParameterDeclarations();

        Type type = parseType();
        Block code = new Block(token.getBeginLocation());

        expectAndConsume(TokenType.LBRACE);
        while (token.getType() != TokenType.RBRACE) {
            code.addStatement(parseStatement());
        }
        expectAndConsumeMaybeEof(TokenType.RBRACE);

        return new FunctionDeclaration(beginning, new Symbol(identifierToken.getText()), access, isStatic, isFinal, type, parameters, code);
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
                    expectAndConsume(TokenType.SEMICOLON);
                }
            }
            default -> {
                // ExpressionStatement | VariableDeclarationStatement | VariableInitializationStatement
                if (token.getType().isPrimitive() || (token.getType() == TokenType.IDENTIFIER && lexer.getLookAhead().getType() == TokenType.LBRACKET && lexer.getLookAhead2().getType() == TokenType.RBRACKET)) {
                    // Variable declaration or initialization
                    statement = parseVariableDeclaration();
                } else {
                    // Expression
                    statement = new ExpressionStatement(token.getBeginLocation(), parseExpression());
                    expectAndConsume(TokenType.SEMICOLON);
                }
            }
        }
        return statement;
    }

    private VariableDeclarationStatement parseVariableDeclaration() throws ParseException {
        Token begin = token;
        Type type = parseType();
        Symbol identifier = new Symbol(token.getText());
        consume();
        if (token.getType() == TokenType.SEMICOLON) {
            VariableDeclarationStatement declaration = new VariableDeclarationStatement(begin.getBeginLocation(), type, identifier);
            expectAndConsume(TokenType.SEMICOLON);
            return declaration;
        } else if (token.getType() == TokenType.ASSIGN) {
            consume();
            VariableInitializationStatement declaration = new VariableInitializationStatement(begin.getBeginLocation(), type, identifier, parseExpression());
            expectAndConsume(TokenType.SEMICOLON);
            return declaration;
        } else {
            throw new ParseException(token.getBeginLocation(), "Expected semicolon or assignment but got " + token.getText());
        }
    }

    private Expression parseExpression() throws ParseException {
        return parseExpression(Integer.MIN_VALUE);
    }

    private Expression parseExpression(int precedence) throws ParseException {
        Expression output = parsePrimaryExpression();
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
                        output = new MethodInvocationExpression(output.location(), output, parameters);
                    }
                    case LBRACKET -> {
                        output = new ArrayIndexExpression(output.location(), output, parseExpression());
                        expectAndConsume(TokenType.RBRACKET);
                    }
                    default -> throw new ParseException(operation.getBeginLocation(), "Unhandled postfix operator " + operation.getText());
                }
            } else {
                throw new ParseException(token.getBeginLocation(), "Unexpected internal state");
            }
            operation = token;
        }

        return output;
    }

    private Expression parsePrimaryExpression() throws ParseException {
        Token begin = token;
        consume();
        Expression expression;
        switch (begin.getType()) {
            case LNOT -> {
                expression = new LogicalInvertExpression(begin.getBeginLocation(), parseExpression());
            }
            case BNOT -> {
                expression = new BinaryInvertExpression(begin.getBeginLocation(), parseExpression());
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
                expression = new ObjectInstantiationExpression(begin.getBeginLocation(), new Symbol(typeToken.getText()), parameters);
            }
            case CHAR_IMMEDIATE -> {
                expression = new CharExpression(begin.getBeginLocation(), begin.getText().substring(1, begin.getText().length() - 1));
            }
            case STRING_IMMEDIATE -> {
                expression = new StringExpression(begin.getBeginLocation(), begin.getText().substring(1, begin.getText().length() - 1));
            }
            case FLOAT_IMMEDIATE -> {
                expression = new FloatExpression(begin.getBeginLocation(), begin.getText());
            }
            case INTEGER_IMMEDIATE -> {
                expression = new IntegerExpression(begin.getBeginLocation(), begin.getText());
            }
            case IDENTIFIER -> {
                expression = new VariableExpression(begin.getBeginLocation(), new Symbol(begin.getText()));
            }
            default -> throw new ParseException(begin.getBeginLocation(), String.format("Unexpected start of expression '%s'", begin.getText()));
        }
        return expression;
    }

    private Type parseType() throws ParseException {
        if (!token.getType().isPrimitive() && !(token.getType() == TokenType.IDENTIFIER)) {
            throw new ParseException(token.getBeginLocation(), "Expected primitive or identifier but got " + token.getType());
        }
        Token type = token;
        consume();
        int arrayLevel = 0;
        while (token.getType() == TokenType.LBRACKET) {
            consume();
            expectAndConsume(TokenType.RBRACKET);
            ++arrayLevel;
        }
        if (type.getType().isPrimitive()) {
            return new Type(type.getType(), arrayLevel);
        } else {
            return new Type(new Symbol(type.toString()), arrayLevel);
        }
    }

    private Access consumeAccessIfPresent(Access defaultAccess) throws ParseException {
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
                return defaultAccess;
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

    private boolean consumeExternIfPresent() throws ParseException{
        if (token.getType() == TokenType.EXTERN) {
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
