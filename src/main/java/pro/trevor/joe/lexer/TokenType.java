package pro.trevor.joe.lexer;

import java.util.Optional;
import java.util.Set;

public enum TokenType {

    ERROR("ERROR"),
    EOF("EOF"),
    COMMENT("COMMENT"),
    IDENTIFIER("IDENTIFIER"),
    CHAR_IMMEDIATE("CHAR_IMMEDIATE"),
    STRING_IMMEDIATE("STRING_IMMEDIATE"),
    INTEGER_IMMEDIATE("INTEGER_IMMEDIATE"),
    FLOAT_IMMEDIATE("FLOAT_IMMEDIATE"),

    BOOL("bool"),
    CHAR("char"),
    F32("f32"),
    F64("f64"),
    I8("i8"),
    I16("i16"),
    I32("i32"),
    I64("i64"),
    U8("u8"),
    U16("u16"),
    U32("u32"),
    U64("u64"),

    ABSTRACT("abstract"),
    ASSERT("assert"),
    BREAK("break"),
    CASE("case"),
    CATCH("catch"),
    CLASS("class"),
    CONST("const"),
    CONTINUE("continue"),
    DEFAULT("default"),
    DO("do"),
    ELSE("else"),
    ENUM("enum"),
    EXTENDS("extends"),
    FALSE("false"),
    FINAL("final"),
    FN("fn"),
    FOR("for"),
    IF("if"),
    IMPL("implements"),
    IMPORT("import"),
    INSTANCEOF("instanceof"),
    INTERFACE("interface"),
    NEW("new"),
    NULL("null"),
    PACKAGE("package"),
    PRIVATE("private"),
    PROTECTED("protected"),
    PUBLIC("public"),
    RETURN("return"),
    STATIC("static"),
    SUPER("super"),
    SWITCH("switch"),
    THIS("this"),
    TRUE("true"),
    TRY("try"),
    VAR("var"),
    VOID("void"),
    WHILE("while"),

    LPAREN("("),
    RPAREN(")"),
    LBRACKET("["),
    RBRACKET("]"),
    LBRACE("{"),
    RBRACE("}"),

    BNOT("~"),
    BAND("&"),
    BOR("|"),

    LNOT("!"),
    LAND("&&"),
    LOR("||"),

    XOR("^"),

    ASSIGN("="),
    NOT_ASSIGN("~="),
    AND_ASSIGN("&="),
    OR_ASSIGN("|="),
    XOR_ASSIGN("^="),
    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),

    EQUALS("=="),
    NOT_EQUALS("!="),
    LESS_THAN("<"),
    LESS_EQUAL("<="),
    GREATER_THAN(">"),
    GREATER_EQUAL(">="),

    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),
    SHIFT_LEFT("<<"),
    SHIFT_RIGHT(">>"),
    SHIFT_RIGHT_LOGICAL(">>>"),

    PERIOD("."),
    COMMA(","),
    SEMICOLON(";"),
    COLON(":"),
    QUESTION("?");

    private static final TokenType[] PRIMITIVES = {BOOL, CHAR, F32, F64, I8, I16, I32, I64, U8, U16, U32, U64};
    private static final TokenType[] KEYWORDS = {
            ABSTRACT, ASSERT, BREAK, CASE, CATCH, CLASS, CONST, CONTINUE, DEFAULT, DO, ELSE, ENUM, EXTENDS, FALSE,
            FINAL, FN, FOR, IF, IMPL, IMPORT, INSTANCEOF, INTERFACE, NEW, NULL, PACKAGE, PRIVATE, PROTECTED, PUBLIC,
            RETURN, STATIC, SUPER, SWITCH, THIS, TRUE, TRY, VAR, VOID, WHILE
    };

    private static final Set<TokenType> PRIMITIVES_SET = Set.of(PRIMITIVES);
    private static final Set<TokenType> KEYWORDS_SET = Set.of(KEYWORDS);

    private final String text;

    TokenType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Optional<TokenType> getKeyword(String string) {
        return KEYWORDS_SET.stream().filter((t) -> string.equals(t.getText())).findFirst();
    }

    public static Optional<TokenType> getPrimitive(String string) {
        return PRIMITIVES_SET.stream().filter((t) -> string.equals(t.getText())).findFirst();
    }

    public boolean isKeyword() {
        return KEYWORDS_SET.contains(this);
    }

    public boolean isPrimitive() {
        return PRIMITIVES_SET.contains(this);
    }

}
