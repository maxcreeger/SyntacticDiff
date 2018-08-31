package lexeme.java.tree.expression.statement.operators.unary;

import lexeme.java.tree.expression.statement.operators.unary.prefix.NotOperator;
import lexeme.java.tree.expression.statement.operators.unary.prefix.PreIncrementOperator;

/**
 * {@link UnaryOperator} visitor.
 *
 * @param <T> the outcome of the visit
 */
public interface UnaryOperatorVisitor<T> {

    /**
     * Visit a {@link PreIncrementOperator}
     * @param preIncrementOperator
     * @return the outcome of the visit
     */
    T visit(PreIncrementOperator preIncrementOperator);

    /**
     * Visit a {@link NotOperator}
     * @param notOperator
     * @return the outcome of the visit
     */
    T visit(NotOperator notOperator);
}
