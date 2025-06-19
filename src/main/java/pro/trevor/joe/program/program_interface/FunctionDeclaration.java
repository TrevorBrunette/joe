package pro.trevor.joe.program.program_interface;

import pro.trevor.joe.program.Parameter;
import pro.trevor.joe.program.type.TypeReference;

import java.util.List;

public record FunctionDeclaration(String identifier, TypeReference returnType, List<Parameter> parameters) {
}
