package pro.trevor.joe.program.program_enum;

import pro.trevor.joe.program.TypeReference;

import java.util.List;

public record Variant(String identifier, List<TypeReference> types) {
}
