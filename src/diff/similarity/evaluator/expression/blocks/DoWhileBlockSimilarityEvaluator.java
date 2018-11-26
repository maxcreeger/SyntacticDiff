package diff.similarity.evaluator.expression.blocks;

import diff.complexity.expression.statement.StatementSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.SimpleSimilarity.ShowableString;
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
 * Compares a {@link DoWhileBlock} to any other {@link AbstractBlock} to produce
 * a {@link Similarity}.
 */
@AllArgsConstructor
@Getter
public class DoWhileBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

	DoWhileBlock rightDoWhileBlock;

	@Override
	public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
		// TODO maybe try enclosing the trycatchfinally with a while?
		ShowableString leftKeyword = new ShowableString("trycatchfinally", leftTryCatchFinallyBlock.getTryBlock().getTryKeyword().getLocation());
		ShowableString rightKeyword = new ShowableString("do", rightDoWhileBlock.getLocation());
		return TryCatchFinallyBlockSimilarityEvaluator.encloseRight(leftTryCatchFinallyBlock, rightDoWhileBlock,
			new SimpleSimilarity(0, 1, leftKeyword, "0", rightKeyword));
	}

	@Override
	public Similarity visit(WhileBlock leftWhileBlock) {
		// TODO try enclosing each into the other?
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftWhileBlock.getWhileKeyword(), "0", rightDoWhileBlock.getDoKeyword());
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftWhileBlock.getEvaluation(), rightDoWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftWhileBlock.getBody(), rightDoWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(IfBlock leftIfBlock) {
		// Match do with THEN block
		Similarity wrongBlockSimilarityThen = new SimpleSimilarity(0, 1, leftIfBlock.getIfKeyword(), "0", rightDoWhileBlock.getDoKeyword());
		Similarity evalDiffThen = StatementSimilarityEvaluator.INSTANCE.eval(leftIfBlock.getCondition(), rightDoWhileBlock.getEvaluation());
		Similarity bodyDiffThen = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftIfBlock.getBody(), rightDoWhileBlock.getBody());
		Similarity simThen = Similarity.add("trans-block", wrongBlockSimilarityThen, evalDiffThen, bodyDiffThen);

		// Match do with ELSE block
		if (leftIfBlock.getElseKeyword().isPresent()) {
			Similarity wrongBlockSimilarityElse = new SimpleSimilarity(0, 1, leftIfBlock.getElseKeyword().get(), "0", rightDoWhileBlock.getDoKeyword());
			Similarity evalDiffElse = StatementSimilarityEvaluator.INSTANCE.eval(leftIfBlock.getCondition(), rightDoWhileBlock.getEvaluation());
			Similarity bodyDiffElse = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftIfBlock.getElseExpressions(), rightDoWhileBlock.getBody());
			Similarity simElse = Similarity.add("trans-block", wrongBlockSimilarityElse, evalDiffElse, bodyDiffElse);
			return Similarity.bestOf(simThen, simElse);
		} else {
			return simThen;
		}
	}

	@Override
	public Similarity visit(ForBlock leftForBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftForBlock.getForKeyword(), "0", rightDoWhileBlock.getDoKeyword());
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftForBlock.getEvaluation(), rightDoWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftForBlock.getBody(), rightDoWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(DoWhileBlock leftDoWhileBlock) {
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftDoWhileBlock.getEvaluation(), rightDoWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftDoWhileBlock.getBody(), rightDoWhileBlock.getBody());
		return Similarity.add("do", evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(PlaceholderBlock leftPlaceholderBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftPlaceholderBlock.getPlaceholderKeyword(), "0", rightDoWhileBlock.getDoKeyword());
		Similarity evalDiff = new LeftLeafSimilarity<>(StatementSizer.STATEMENT_SIZER.size(rightDoWhileBlock.getEvaluation()),
			rightDoWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftPlaceholderBlock.getBody(), rightDoWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}
}
