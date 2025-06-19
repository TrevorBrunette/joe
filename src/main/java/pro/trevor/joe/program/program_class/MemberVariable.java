package pro.trevor.joe.program.program_class;

import pro.trevor.joe.parser.tree.declaration.Access;
import pro.trevor.joe.program.type.TypeReference;

public record MemberVariable(Access access, boolean isStatic, boolean isFinal, TypeReference type, String identifier) {
}
