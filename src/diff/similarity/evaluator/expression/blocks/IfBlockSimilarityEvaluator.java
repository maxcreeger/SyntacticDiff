package diff.similarity.evaluator.expression.blocks;

import java.util.stream.Collectors;

import diff.complexity.expression.ExpressionSizer;
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
 * Compares a {@link IfBlock} to any other {@link AbstractBlock} to produce a
 * {@link Similarity}.
 */
@AllArgsConstructor
@Getter
public class IfBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

	IfBlock rightIfBlock;

	@Override
	public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
		return TryCatchFinallyBlockSimilarityEvaluator.encloseRight(leftTryCatchFinallyBlock, rightIfBlock,
			new SimpleSimilarity(0, 1, leftTryCatchFinallyBlock.getTryBlock().getTryKeyword(), "0", rightIfBlock.getIfKeyword()));
	}

	@Override
	public Similarity visit(WhileBlock leftWhileBlock) {
		Similarity condition = StatementSimilarityEvaluator.INSTANCE.eval(leftWhileBlock.getEvaluation(), rightIfBlock.getCondition());

		// Attempt to match the "while body" with "Then Body"
		Similarity whileThen = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftWhileBlock.getBody(), rightIfBlock.getThenExpressions());
		Similarity nothingElse = Similarity.add("else",
			rightIfBlock.getElseExpressions().stream().map(elseExpr -> RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(elseExpr), elseExpr))
						.collect(Collectors.toList()));
		Similarity whileVersusThen = Similarity.add("trans-block",
			new SimpleSimilarity(0, 1, leftWhileBlock.getWhileKeyword(), "0", rightIfBlock.getIfKeyword()), condition, whileThen, nothingElse);

		// Attempt to match the "while body" with "else body"
		if (rightIfBlock.getElseKeyword().isPresent()) {
			Similarity nothingThen = Similarity.add("else",
				rightIfBlock.getThenExpressions().stream().map(elseExpr -> RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(elseExpr), elseExpr))
							.collect(Collectors.toList()));
			Similarity whileElse = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftWhileBlock.getBody(), rightIfBlock.getThenExpressions());
			Similarity whileVersusElse = Similarity.add("trans-block",
				new SimpleSimilarity(0, 1, leftWhileBlock.getWhileKeyword(), "0", rightIfBlock.getElseKeyword().get()), condition, nothingThen, whileElse);
			// Return
			return Similarity.bestOf(whileVersusThen, whileVersusElse);
		} else {
			return whileVersusThen;
		}
	}

	@Override
	public Similarity visit(IfBlock leftIfBlock) {
		Similarity condDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftIfBlock.getCondition(), rightIfBlock.getCondition());
		Similarity thenDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftIfBlock.getThenExpressions(), rightIfBlock.getThenExpressions());
		Similarity elseDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftIfBlock.getElseExpressions(), rightIfBlock.getElseExpressions());
		return Similarity.add("if", condDiff, thenDiff, elseDiff);
	}

	@Override
	public Similarity visit(ForBlock leftForBlock) {
		Similarity init = ExpressionSimilarityEvaluator.INSTANCE.eval(leftForBlock.getInitialisation(), rightIfBlock.getCondition());
		Similarity iter = ExpressionSimilarityEvaluator.INSTANCE.eval(leftForBlock.getIteration(), rightIfBlock.getCondition());
		Similarity condition = ExpressionSimilarityEvaluator.INSTANCE.eval(leftForBlock.getEvaluation(), rightIfBlock.getCondition());

		// Attempt to match the "while body" with "Then Body"
		Similarity whileThen = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftForBlock.getBody(), rightIfBlock.getThenExpressions());
		Similarity nothingElse = Similarity.add("else",
			rightIfBlock.getElseExpressions().stream().map(elseExpr -> RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(elseExpr), elseExpr))
						.collect(Collectors.toList()));
		Similarity whileVersusThen = Similarity.add("trans-block", new SimpleSimilarity(0, 1, leftForBlock.getForKeyword(), "0", rightIfBlock.getIfKeyword()),
			init, iter, condition, whileThen, nothingElse);

		// Attempt to match the "while body" with "else body"
		if (rightIfBlock.getElseKeyword().isPresent()) {
			Similarity nothingThen = Similarity.add("else",
				rightIfBlock.getThenExpressions().stream().map(elseExpr -> RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(elseExpr), elseExpr))
							.collect(Collectors.toList()));
			Similarity whileElse = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftForBlock.getBody(), rightIfBlock.getElseExpressions());
			Similarity whileVersusElse = Similarity.add("trans-block",
				new SimpleSimilarity(0, 1, leftForBlock.getForKeyword(), "0", rightIfBlock.getElseKeyword().get()), init, iter, condition, nothingThen,
				whileElse);
			// Return
			return Similarity.bestOf(whileVersusThen, whileVersusElse);
		} else {
			return whileVersusThen;
		}
	}

	@Override
	public Similarity visit(DoWhileBlock leftDoWhileBlock) {
		// Attempt to match the "while body" with "Then Body"
		Similarity condition = StatementSimilarityEvaluator.INSTANCE.eval(leftDoWhileBlock.getEvaluation(), rightIfBlock.getCondition());
		Similarity whileThen = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftDoWhileBlock.getBody(), rightIfBlock.getThenExpressions());
		Similarity nothingElse = Similarity.add("else",
			rightIfBlock.getElseExpressions().stream().map(elseExpr -> RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(elseExpr), elseExpr))
						.collect(Collectors.toList()));
		Similarity whileVersusThen = Similarity.add("trans-block",
			new SimpleSimilarity(0, 1, leftDoWhileBlock.getDoKeyword(), "0", rightIfBlock.getIfKeyword()), condition, whileThen, nothingElse);

		// Attempt to match the "while body" with "else body"
		if (rightIfBlock.getElseKeyword().isPresent()) {
			Similarity nothingThen = Similarity.add("else",
				rightIfBlock.getThenExpressions().stream().map(elseExpr -> RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(elseExpr), elseExpr))
							.collect(Collectors.toList()));
			Similarity whileElse = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftDoWhileBlock.getBody(), rightIfBlock.getElseExpressions());
			Similarity whileVersusElse = Similarity.add("trans-block",
				new SimpleSimilarity(0, 1, leftDoWhileBlock.getDoKeyword(), "0", rightIfBlock.getElseKeyword().get()), condition, nothingThen, whileElse);
			// Return
			return Similarity.bestOf(whileVersusThen, whileVersusElse);
		} else {
			return whileVersusThen;
		}
	}

	@Override
	public Similarity visit(PlaceholderBlock leftPlaceholderBlock) {
		// Attempt to match the "while body" with "Then Body"
		Similarity whileThen = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftPlaceholderBlock.getBody(), rightIfBlock.getThenExpressions());
		Similarity nothingElse = Similarity.add("else",
			rightIfBlock.getElseExpressions().stream().map(elseExpr -> RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(elseExpr), elseExpr))
						.collect(Collectors.toList()));
		Similarity whileVersusThen = Similarity.add("trans-block",
			new SimpleSimilarity(0, 1, leftPlaceholderBlock.getPlaceholderKeyword(), "0", rightIfBlock.getIfKeyword()), whileThen, nothingElse);

		if (rightIfBlock.getElseKeyword().isPresent()) {
			// Attempt to match the "while body" with "else body"
			Similarity nothingThen = Similarity.add("else",
				rightIfBlock.getThenExpressions().stream().map(elseExpr -> RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(elseExpr), elseExpr))
							.collect(Collectors.toList()));
			Similarity whileElse = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftPlaceholderBlock.getBody(), rightIfBlock.getElseExpressions());
			Similarity whileVersusElse = Similarity.add("trans-block",
				new SimpleSimilarity(0, 1, leftPlaceholderBlock.getPlaceholderKeyword(), "0", rightIfBlock.getElseKeyword().get()), nothingThen, whileElse);
			// Return
			return Similarity.bestOf(whileVersusThen, whileVersusElse);
		} else {
			return whileVersusThen;
		}
	}
}
