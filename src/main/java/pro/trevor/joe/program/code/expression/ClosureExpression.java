package pro.trevor.joe.program.code.expression;

import pro.trevor.joe.program.Parameter;
import pro.trevor.joe.program.code.Expression;
import pro.trevor.joe.program.code.Statement;

import java.util.List;

public record ClosureExpression(List<Parameter> parameters, Statement statement) implements Expression {
}
