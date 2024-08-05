package pro.trevor.joe.tree.expression.literal;

import pro.trevor.joe.lexer.Location;
import pro.trevor.joe.tree.IVisitor;
import pro.trevor.joe.tree.Symbol;
import pro.trevor.joe.tree.expression.Expression;
import pro.trevor.joe.tree.expression.type.ClassType;
import pro.trevor.joe.tree.expression.type.ReturnType;
import pro.trevor.joe.tree.expression.type.Type;

public class ThisExpression extends LiteralExpression {

    private final Symbol classSymbol;

    public ThisExpression(Location location, Symbol classSymbol) {
        super(location);
        this.classSymbol = classSymbol;
    }

    @Override
    public Type type() {
        return new ClassType(classSymbol);
    }
}
