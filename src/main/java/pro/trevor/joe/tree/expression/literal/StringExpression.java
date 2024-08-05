package pro.trevor.joe.tree.expression.literal;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.IVisitor;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ClassType;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class StringExpression extends LiteralExpression {

    private final String value;

    public StringExpression(Location location, String value) {
        super(location);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Type type() {
        // TODO figure something out for this
        return new ClassType(new Symbol(null, "java/lang/String"));
    }
}
