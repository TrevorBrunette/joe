package pro.trevor.joe.program.type;

public enum Primitive {
    VOID(Integer.MAX_VALUE),
    BOOL(1),
    U8(2),
    U16(3),
    U32(4),
    U64(5),
    U128(6),
    I8(2),
    I16(3),
    I32(4),
    I64(5),
    I128(6),
    F32(7),
    F64(8);

    private final int weight;
    Primitive(int weight) {
        this.weight = weight;
    }

    public static Primitive arithmetic(Primitive left, Primitive right) {
        if (left.weight == right.weight) {
            switch (left) {
                case I8 -> {
                    return I8;
                }
                case I16 -> {
                    return I16;
                }
                case I32 -> {
                    return I32;
                }
                case I64 -> {
                    return I64;
                }
                case I128 -> {
                    return I128;
                }
            }
            switch (right) {
                case I8 -> {
                    return I8;
                }
                case I16 -> {
                    return I16;
                }
                case I32 -> {
                    return I32;
                }
                case I64 -> {
                    return I64;
                }
                case I128 -> {
                    return I128;
                }
            }
            return left;
        } else if (left.weight > right.weight) {
            return  left;
        } else {
            return right;
        }
    }

    public boolean isInt() {
        switch (this) {
            case U8, I8, U16, I16, U32, I32, U64, I64, U128, I128 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isSigned() {
        switch (this) {
            case I8, I16, I32, I64, I128 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isUnsigned() {
        switch (this) {
            case U8, U16, U32, U64, U128 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isFloat() {
        switch (this) {
            case F32, F64 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isNumber() {
        switch (this) {
            case U8, I8, U16, I16, U32, I32, U64, I64, U128, I128, F32, F64 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
