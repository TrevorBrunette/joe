package pro.trevor.joe.tree.expression.type;

import pro.trevor.joe.lexer.Token;
import pro.trevor.joe.lexer.TokenType;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.declaration.Declaration;

import java.util.Optional;

public enum ReturnType {
    NULL(0),
    OBJECT(0),
    ARRAY(0),
    VOID(0),
    BOOLEAN(1),
    U8(2),
    U16(4),
    U32(6),
    U64(9),
    I8(3),
    I16(5),
    I32(7),
    I64(10),
    F32(8),
    F64(11);

    private final int relativeSize;

    ReturnType(int relativeSize) {
        this.relativeSize = relativeSize;
    }

    public boolean isUnsigned() {
        return switch (this) {
            case U8, U16, U32, U64 -> true;
            default -> false;
        };
    }

    public boolean isSigned() {
        return switch (this) {
            case I8, I16, I32, I64 -> true;
            default -> false;
        };
    }

    public boolean isInteger() {
        return switch (this) {
            case U8, U16, U32, U64, I8, I16, I32, I64 -> true;
            default -> false;
        };
    }

    public boolean isNumber() {
        return switch (this) {
            case U8, U16, U32, U64, I8, I16, I32, I64, F32, F64 -> true;
            default -> false;
        };
    }

    public static Optional<ReturnType> returnTypeFromToken(TokenType type) {
        return Optional.ofNullable(switch (type) {
            case VOID -> VOID;
            case BOOL -> BOOLEAN;
            case U8 -> U8;
            case U16, CHAR -> U16;
            case U32 -> U32;
            case U64 -> U64;
            case I8 -> I8;
            case I16 -> I16;
            case I32 -> I32;
            case I64 -> I64;
            case F32 -> F32;
            case F64 -> F64;
            case IDENTIFIER -> OBJECT;
            default -> null;
        });
    }

    public static Optional<Type> typeFromToken(Token token, Declaration parent) {
        Optional<ReturnType> optionalReturnType = returnTypeFromToken(token.getType());
        if (optionalReturnType.isEmpty()) {
            return Optional.empty();
        }
        ReturnType returnType = optionalReturnType.get();
        if (returnType == OBJECT) {
            return Optional.of(new ClassType(new Symbol(parent, token.getText())));
        } else {
            return Optional.of(new Type(returnType));
        }
    }

    public static ReturnType highest(ReturnType left, ReturnType right) {
        errorIncompatibleTypes(left);
        errorIncompatibleTypes(right);
        return left.relativeSize > right.relativeSize ? left : right;
    }

    private static void error(ReturnType type) {
        throw new Error(type.name() + " not expected to be compared");
    }

    private static void errorIncompatibleTypes(ReturnType type) {
        if (type.relativeSize == 0) {
            error(type);
        }
    }
}
