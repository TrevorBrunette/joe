package pro.trevor.joe.program.llvm;

import pro.trevor.joe.program.code.Expressions;
import pro.trevor.joe.program.type.Primitive;
import pro.trevor.joe.program.type.PrimitiveTypeReference;

import java.util.Optional;

public class Constants {

    public enum CompOpCode {
        FEQ(1),
        FGT(2),
        FGE(3),
        FLT(4),
        FLE(5),
        FNE(6),
        IEQ(32),
        INE(33),
        UGT(34),
        UGE(35),
        ULT(36),
        ULE(37),
        SGT(38),
        SGE(39),
        SLT(40),
        SLE(41);
        final int op;
        CompOpCode(int op) {
            this.op = op;
        }

        public static Optional<CompOpCode> opCodeFor(Expressions.BinaryExpression expression, PrimitiveTypeReference leftType, PrimitiveTypeReference rightType) {
            Primitive resultType = Primitive.arithmetic(leftType.primitive(), rightType.primitive());
            CompOpCode opCode = null;
            if (resultType.isFloat()) {
                opCode = switch (expression) {
                    case Expressions.Equals ignored -> FEQ;
                    case Expressions.GreaterThan ignored -> FGT;
                    case Expressions.GreaterThanOrEquals ignored -> FGE;
                    case Expressions.LessThan ignored -> FLT;
                    case Expressions.LessThanOrEquals ignored -> FLE;
                    case Expressions.NotEquals ignored -> FNE;
                    default -> null;
                };
            } else if (resultType.isSigned()) {
                opCode = switch (expression) {
                    case Expressions.Equals ignored -> IEQ;
                    case Expressions.GreaterThan ignored -> SGT;
                    case Expressions.GreaterThanOrEquals ignored -> SGE;
                    case Expressions.LessThan ignored -> SLT;
                    case Expressions.LessThanOrEquals ignored -> SLE;
                    case Expressions.NotEquals ignored -> INE;
                    default -> null;
                };
            } else if (resultType.isUnsigned()) {
                opCode = switch (expression) {
                    case Expressions.Equals ignored -> IEQ;
                    case Expressions.GreaterThan ignored -> UGT;
                    case Expressions.GreaterThanOrEquals ignored -> UGE;
                    case Expressions.LessThan ignored -> ULT;
                    case Expressions.LessThanOrEquals ignored -> ULE;
                    case Expressions.NotEquals ignored -> INE;
                    default -> null;
                };
            }
            return Optional.ofNullable(opCode);
        }
    }

}
