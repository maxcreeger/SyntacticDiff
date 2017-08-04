package parser.syntaxtree.expression.statement.operators;

import parser.syntaxtree.expression.statement.operators.binary.BinaryOperator;
import parser.syntaxtree.expression.statement.operators.unary.UnaryOperator;

/**
 * Visits an operator.
 *
 * @param <T> the type of the outcome
 */
public interface OperatorVisitor<T> {

    /**
     * Visits a {@link UnaryOperator}.
     * @param unaryOperator
     * @return the outcome
     */
    T visit(UnaryOperator unaryOperator);

    /**
     * Visits a {@link BinaryOperator}.
     * @param binaryOperator
     * @return the outcome
     */
    T visit(BinaryOperator binaryOperator);
}
