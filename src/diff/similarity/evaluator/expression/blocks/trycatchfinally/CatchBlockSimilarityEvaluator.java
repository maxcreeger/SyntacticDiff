package diff.similarity.evaluator.expression.blocks.trycatchfinally;

import diff.complexity.expression.blocks.trycatchfinally.CatchBlockSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.ClassNameSimilarityEvaluator;
import diff.similarity.evaluator.SimilarityEvaluator;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.VariableReferenceSimilarityEvaluator;
import lexeme.java.tree.expression.blocks.trycatchfinally.CatchBlock;

/**
 * Compares two {@link CatchBlock}s.
 */
public class CatchBlockSimilarityEvaluator extends SimilarityEvaluator<CatchBlock> {

	/** Instance */
	public static final CatchBlockSimilarityEvaluator INSTANCE = new CatchBlockSimilarityEvaluator();

	private CatchBlockSimilarityEvaluator() {
		super(CatchBlockSizer.CATCH_BLOCK_SIZER, "catch");
	}

	@Override
	public Similarity eval(CatchBlock obj1, CatchBlock obj2) {
		Similarity exceptionTypes = ClassNameSimilarityEvaluator.INSTANCE.maximumMatch(obj1.getExceptionTypes(), obj2.getExceptionTypes());
		Similarity exceptionName = VariableReferenceSimilarityEvaluator.INSTANCE.eval(obj1.getExceptionReference(), obj2.getExceptionReference());
		Similarity catchBody = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(obj1.getCatchExpressions(), obj2.getCatchExpressions());
		return Similarity.add("Catch", exceptionTypes, exceptionName, catchBody);
	}
}
