package lexeme.java.tree.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an empty expression like <code>;;</code> or the absences of a parameter in a method call.
 */
public class EmptyExpression extends Expression {

    @Override
    public <T> T acceptExpressionVisitor(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<String> show(String prefix) {
        final ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(prefix);
        return arrayList;
    }

}
