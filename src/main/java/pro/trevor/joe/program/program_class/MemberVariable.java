package pro.trevor.joe.program.program_class;

import pro.trevor.joe.program.Access;
import pro.trevor.joe.program.TypeReference;

public record MemberVariable(Access access, boolean isStatic, boolean isFinal, TypeReference type, String identifier) {

}
