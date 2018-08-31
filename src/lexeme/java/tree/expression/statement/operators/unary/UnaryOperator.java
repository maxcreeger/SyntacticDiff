package lexeme.java.tree.expression.statement.operators.unary;

import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.operators.Operator;
import lexeme.java.tree.expression.statement.operators.OperatorVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Unary Operator like <code>!</code>.
 */
@Getter
@AllArgsConstructor
public abstract class UnaryOperator extends Operator {

    protected final Statement targetedStatement;

    @Override
    public <T> T acceptOperatorVisitor(OperatorVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Accept a {@link UnaryOperatorVisitor}.
     * @param <T> the outcome type of the visit
     * @param visitor the visitor
     * @return the outcome of the visit
     */
    public abstract <T> T acceptUnaryOperatorVisitor(UnaryOperatorVisitor<T> visitor);

}
