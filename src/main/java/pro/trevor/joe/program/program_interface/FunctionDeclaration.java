package pro.trevor.joe.program.program_interface;

import pro.trevor.joe.program.Parameter;

import java.util.List;

public record FunctionDeclaration(String identifier, List<Parameter> parameters) {
}
