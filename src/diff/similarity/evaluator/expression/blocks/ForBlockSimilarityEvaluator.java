package diff.similarity.evaluator.expression.blocks;

import diff.complexity.expression.ExpressionSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.StatementSimilarityEvaluator;
import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.blocks.BlockVisitor;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Compares a {@link ForBlock} to any other {@link AbstractBlock} to produce a
 * {@link Similarity}.
 */
@AllArgsConstructor
@Getter
public class ForBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

	ForBlock rightForBlock;

	@Override
	public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
		return TryCatchFinallyBlockSimilarityEvaluator.encloseRight(leftTryCatchFinallyBlock, rightForBlock,
			new SimpleSimilarity(0, 1, leftTryCatchFinallyBlock.getTryBlock().getTryKeyword(), "0", rightForBlock.getForKeyword()));
	}

	@Override
	public Similarity visit(WhileBlock leftWhileBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftWhileBlock.getWhileKeyword(), "0", rightForBlock.getForKeyword());
		Similarity initDiff = new RightLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(rightForBlock.getInitialisation()),
			rightForBlock.getInitialisation());
		Similarity iterDiff = new RightLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(rightForBlock.getIteration()), rightForBlock.getIteration());
		Similarity evalDiff = ExpressionSimilarityEvaluator.INSTANCE.eval(leftWhileBlock.getEvaluation(), rightForBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftWhileBlock.getBody(), rightForBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, initDiff, iterDiff, evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(IfBlock leftIfBlock) {
		Similarity initDiff = new RightLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(rightForBlock.getInitialisation()),
			rightForBlock.getInitialisation());
		Similarity iterDiff = new RightLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(rightForBlock.getIteration()), rightForBlock.getIteration());

		// Match for with THEN block
		Similarity evalDiffThen = ExpressionSimilarityEvaluator.INSTANCE.eval(leftIfBlock.getCondition(), rightForBlock.getEvaluation());
		Similarity bodyDiffThen = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftIfBlock.getBody(), rightForBlock.getBody());
		Similarity simThen = Similarity.add("trans-block", new SimpleSimilarity(0, 1, leftIfBlock.getIfKeyword(), "0", rightForBlock.getForKeyword()), initDiff,
			iterDiff, evalDiffThen, bodyDiffThen);

		// Match for with ELSE block
		if (leftIfBlock.getElseKeyword().isPresent()) {
			Similarity evalDiffElse = ExpressionSimilarityEvaluator.INSTANCE.eval(leftIfBlock.getCondition(), rightForBlock.getEvaluation());
			Similarity bodyDiffElse = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftIfBlock.getElseExpressions(), rightForBlock.getBody());
			Similarity simElse = Similarity.add("trans-block",
				new SimpleSimilarity(0, 1, leftIfBlock.getElseKeyword().get(), "0", rightForBlock.getForKeyword()), initDiff, iterDiff, evalDiffElse,
				bodyDiffElse);
			return Similarity.bestOf(simThen, simElse);
		} else {
			return simThen;
		}
	}

	@Override
	public Similarity visit(ForBlock leftForBlock) {
		Similarity initDiff = ExpressionSimilarityEvaluator.INSTANCE.eval(leftForBlock.getInitialisation(), rightForBlock.getInitialisation());
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftForBlock.getEvaluation(), rightForBlock.getEvaluation());
		Similarity iterDiff = ExpressionSimilarityEvaluator.INSTANCE.eval(leftForBlock.getIteration(), rightForBlock.getIteration());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftForBlock.getBody(), rightForBlock.getBody());
		return Similarity.add("for", initDiff, evalDiff, iterDiff, bodyDiff);
	}

	@Override
	public Similarity visit(DoWhileBlock leftDoWhileBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftDoWhileBlock.getDoKeyword(), "0", rightForBlock.getForKeyword());
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftDoWhileBlock.getEvaluation(), rightForBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftDoWhileBlock.getBody(), rightForBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(PlaceholderBlock leftPlaceholderBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftPlaceholderBlock.getPlaceholderKeyword(), "0", rightForBlock.getForKeyword());
		Similarity evalDiff = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(rightForBlock.getEvaluation()), rightForBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftPlaceholderBlock.getBody(), rightForBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}

}
