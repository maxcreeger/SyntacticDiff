package diff.similarity.evaluator.expression.blocks;

import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.blocks.BlockVisitor;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import diff.complexity.expression.ExpressionSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.StatementSimilarityEvaluator;

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
		return TryCatchFinallyBlockSimilarityEvaluator.encloseRight(leftTryCatchFinallyBlock, rightWhileBlock, new SimpleSimilarity(0, 1, "trycatchfinally",
				"0", "while"));
	}

	@Override
	public Similarity visit(WhileBlock leftWhileBlock) {
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftWhileBlock.getEvaluation(), rightWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftWhileBlock.getBody(), rightWhileBlock.getBody());
		return Similarity.add("while", evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(IfBlock leftIfBlock) {
		// Match placeholder with THEN block
		Similarity wrongBlockSimilarityThen = new SimpleSimilarity(0, 1, "if-then", "0", "while");
		Similarity evalDiffThen = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftIfBlock.getCondition()), leftIfBlock.getCondition());
		Similarity bodyDiffThen = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getBody(), rightWhileBlock.getBody());
		Similarity simThen = Similarity.add("trans-block", wrongBlockSimilarityThen, evalDiffThen, bodyDiffThen);
		// Match placeholder with ELSE block
		Similarity wrongBlockSimilarityElse = new SimpleSimilarity(0, 1, "if-else", "0", "while");
		Similarity evalDiffElse = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftIfBlock.getCondition()), leftIfBlock.getCondition());
		Similarity bodyDiffElse = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getElseExpressions(), rightWhileBlock.getBody());
		Similarity simElse = Similarity.add("trans-block", wrongBlockSimilarityElse, evalDiffElse, bodyDiffElse);
		return Similarity.bestOf(simThen, simElse);
	}

	@Override
	public Similarity visit(ForBlock leftForBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "for", "0", "while");
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftForBlock.getEvaluation(), rightWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftForBlock.getBody(), rightWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(DoWhileBlock leftDoWhileBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "do", "0", "while");
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftDoWhileBlock.getEvaluation(), rightWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftDoWhileBlock.getBody(), rightWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(PlaceholderBlock leftPlaceholderBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "<nothing>", "0", "while");
		Similarity evalDiff = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(rightWhileBlock.getEvaluation()), rightWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftPlaceholderBlock.getBody(), rightWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}
}
