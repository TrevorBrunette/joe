package pro.trevor.joe.program.code;

import pro.trevor.joe.program.type.NamedTypeReference;

import java.util.List;

public record ClassImplementation(NamedTypeReference type, List<Function> functions) {
}