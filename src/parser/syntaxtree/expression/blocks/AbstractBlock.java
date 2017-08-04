package parser.syntaxtree.expression.blocks;

import java.util.List;

import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.ExpressionVisitor;

/**
 * Represents a block of code (while, a for, a try...)
 */
public abstract class AbstractBlock extends Expression {

    @Override
    public <T> T acceptExpressionVisitor(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Accepts a {@link BlockVisitor}
     * @param <T> the return type
     * @param visitor the visitor
     * @return the outcome
     */
    public abstract <T> T acceptBlockVisitor(BlockVisitor<T> visitor);

    /**
     * Return this block's main body section.
     * @return a {@link List} of {@link Expression}s
     */
    public abstract List<Expression> getBody();


}
