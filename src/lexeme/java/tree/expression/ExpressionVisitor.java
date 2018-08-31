package lexeme.java.tree.expression;

import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.statement.Statement;

/**
 * Visitor for {@link Expression}s.
 * @param <T> the type of result returned by the visitor
 */
public interface ExpressionVisitor<T> {

    /**
     * Visits an {@link EmptyExpression}.
     * @param emptyExpression
     * @return the result
     */
    T visit(EmptyExpression emptyExpression);


    /**
     * Visits an {@link AbstractBlock}.
     * @param block
     * @return the result
     */
    T visit(AbstractBlock block);


    /**
     * Visits an {@link VariableDeclaration}.
     * @param variable
     * @return the result
     */
    T visit(VariableDeclaration variable);


    /**
     * Visits an {@link Statement}.
     * @param statement
     * @return the result
     */
    T visit(Statement statement);

}
