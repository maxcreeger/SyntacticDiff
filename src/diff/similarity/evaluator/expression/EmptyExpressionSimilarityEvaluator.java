package diff.similarity.evaluator.expression;

import diff.complexity.expression.EmptyExpressionSizer;
import diff.similarity.ExpressionSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import lexeme.java.tree.expression.EmptyExpression;

/**
 * Compares two {@link EmptyExpression}s.
 */
public class EmptyExpressionSimilarityEvaluator extends SimilarityEvaluator<EmptyExpression> {


    /** Instance. */
    public static final EmptyExpressionSimilarityEvaluator INSTANCE = new EmptyExpressionSimilarityEvaluator();

    private EmptyExpressionSimilarityEvaluator() {
        super(EmptyExpressionSizer.EMPTY_EXPRESSION_SIZER, "empty");
    }

    public Similarity eval(EmptyExpression empty1, EmptyExpression empty2) {
        return new ExpressionSimilarity<>(1, 1, empty1, empty2);
    }
}
