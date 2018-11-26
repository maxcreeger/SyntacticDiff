package diff.similarity.evaluator.expression.blocks;

import diff.complexity.expression.ExpressionSizer;
import diff.similarity.LeftLeafSimilarity;
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
 * Compares a {@link WhileBlock} to any other {@link AbstractBlock} to produce a
 * {@link Similarity}.
 */
@AllArgsConstructor
@Getter
public class WhileBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

	WhileBlock rightWhileBlock;

	@Override
	public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
		return TryCatchFinallyBlockSimilarityEvaluator.encloseRight(leftTryCatchFinallyBlock, rightWhileBlock,
			new SimpleSimilarity(0, 1, leftTryCatchFinallyBlock.getTryBlock().getTryKeyword(), "0", rightWhileBlock.getWhileKeyword()));
	}

	@Override
	public Similarity visit(WhileBlock leftWhileBlock) {
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftWhileBlock.getEvaluation(), rightWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftWhileBlock.getBody(), rightWhileBlock.getBody());
		return Similarity.add("while", evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(IfBlock leftIfBlock) {
		// Match rightWhileBlock with THEN block
		Similarity wrongBlockSimilarityThen = Similarity.eval(leftIfBlock.getIfKeyword(), rightWhileBlock.getWhileKeyword());
		Similarity evalDiffThen = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftIfBlock.getCondition()), leftIfBlock.getCondition());
		Similarity bodyDiffThen = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftIfBlock.getBody(), rightWhileBlock.getBody());
		Similarity simThen = Similarity.add("trans-block", wrongBlockSimilarityThen, evalDiffThen, bodyDiffThen);
		if (leftIfBlock.getElseKeyword().isPresent()) {
			// Match rightWhileBlock with ELSE block
			Similarity wrongBlockSimilarityElse = Similarity.eval(leftIfBlock.getElseKeyword().get(), rightWhileBlock.getWhileKeyword());
			Similarity evalDiffElse = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftIfBlock.getCondition()), leftIfBlock.getCondition());
			Similarity bodyDiffElse = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftIfBlock.getElseExpressions(), rightWhileBlock.getBody());
			Similarity simElse = Similarity.add("trans-block", wrongBlockSimilarityElse, evalDiffElse, bodyDiffElse);
			return Similarity.bestOf(simThen, simElse);
		} else {
			return simThen;
		}
	}

	@Override
	public Similarity visit(ForBlock leftForBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftForBlock.getForKeyword(), "0", rightWhileBlock.getWhileKeyword());
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftForBlock.getEvaluation(), rightWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftForBlock.getBody(), rightWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(DoWhileBlock leftDoWhileBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftDoWhileBlock.getDoKeyword(), "0", rightWhileBlock.getWhileKeyword());
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftDoWhileBlock.getEvaluation(), rightWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftDoWhileBlock.getBody(), rightWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(PlaceholderBlock leftPlaceholderBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftPlaceholderBlock.getPlaceholderKeyword(), "0", rightWhileBlock.getWhileKeyword());
		Similarity evalDiff = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(rightWhileBlock.getEvaluation()), rightWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftPlaceholderBlock.getBody(), rightWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}
}
