package diff.similarity.evaluator.expression;

import diff.complexity.expression.ExpressionSizer;
import diff.similarity.ExpressionSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import parser.syntaxtree.expression.EmptyExpression;
import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.ExpressionVisitor;
import parser.syntaxtree.expression.VariableDeclaration;
import parser.syntaxtree.expression.blocks.AbstractBlock;
import parser.syntaxtree.expression.statement.Statement;

/**
 * Compares two {@link Expression}s.
 */
public class ExpressionSimilarityEvaluator extends SimilarityEvaluator<Expression>
        implements
            ExpressionVisitor<DualExpressionComparator<? extends Expression>> {

    /** Instance. */
    public static final ExpressionSimilarityEvaluator INSTANCE = new ExpressionSimilarityEvaluator();

    private ExpressionSimilarityEvaluator() {
        super(ExpressionSizer.EXPRESSION_SIZER, "expr");
    }

    public Similarity eval(Expression expr1, Expression expr2) {
        final boolean exactSameClass = expr1.getClass().equals(expr2.getClass());
        final boolean bothAbstractBlocks = AbstractBlock.class.isInstance(expr1) && AbstractBlock.class.isInstance(expr2);
        if (exactSameClass || bothAbstractBlocks) {
            DualExpressionComparator<? extends Expression> firstPass = expr2.acceptExpressionVisitor(this);
            return expr1.acceptExpressionVisitor(firstPass);
        } else {
            return new ExpressionSimilarity<>(0, ExpressionSizer.EXPRESSION_SIZER.size(expr1, expr2), expr1, expr2);
        }
    }

    @Override
    public DualExpressionComparator<EmptyExpression> visit(EmptyExpression emptyExpression) {
        return new DualExpressionComparator<EmptyExpression>(emptyExpression);
    }

    @Override
    public DualExpressionComparator<AbstractBlock> visit(AbstractBlock block) {
        return new DualExpressionComparator<AbstractBlock>(block);
    }

    @Override
    public DualExpressionComparator<VariableDeclaration> visit(VariableDeclaration varDecla) {
        return new DualExpressionComparator<VariableDeclaration>(varDecla);
    }

    @Override
    public DualExpressionComparator<Statement> visit(Statement statement) {
        return new DualExpressionComparator<Statement>(statement);
    }

}
