package diff.similarity.evaluator.expression.blocks.trycatchfinally;

import diff.complexity.expression.blocks.trycatchfinally.FinallyBlockSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import lexeme.java.tree.expression.blocks.trycatchfinally.FinallyBlock;

/**
 * Compares two {@link FinallyBlock}s.
 */
public class FinallyBlockSimilarityEvaluator extends SimilarityEvaluator<FinallyBlock> {

	/** Instance */
	public static final FinallyBlockSimilarityEvaluator INSTANCE = new FinallyBlockSimilarityEvaluator();

	private FinallyBlockSimilarityEvaluator() {
		super(FinallyBlockSizer.FINALLY_BLOCK_SIZER, "finally");
	}

	@Override
	public Similarity eval(FinallyBlock obj1, FinallyBlock obj2) {
		return ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(obj1.getFinallyExpressions(), obj2.getFinallyExpressions());
	}

}
