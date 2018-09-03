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
import diff.complexity.expression.statement.StatementSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.StatementSimilarityEvaluator;

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
		return TryCatchFinallyBlockSimilarityEvaluator.encloseRight(leftTryCatchFinallyBlock, rightDoWhileBlock, new SimpleSimilarity(0, 1, "trycatchfinally",
			"0", "do"));
	}

	@Override
	public Similarity visit(WhileBlock leftWhileBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "while", "0", "do");
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftWhileBlock.getEvaluation(), rightDoWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftWhileBlock.getBody(), rightDoWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(IfBlock leftIfBlock) {
		// Match do with THEN block
		Similarity wrongBlockSimilarityThen = new SimpleSimilarity(0, 1, "if-then", "0", "do");
		Similarity evalDiffThen = StatementSimilarityEvaluator.INSTANCE.eval(leftIfBlock.getCondition(), rightDoWhileBlock.getEvaluation());
		Similarity bodyDiffThen = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getBody(), rightDoWhileBlock.getBody());
		Similarity simThen = Similarity.add("trans-block", wrongBlockSimilarityThen, evalDiffThen, bodyDiffThen);

		// Match do with ELSE block
		Similarity wrongBlockSimilarityElse = new SimpleSimilarity(0, 1, "if-else", "0", "do");
		Similarity evalDiffElse = StatementSimilarityEvaluator.INSTANCE.eval(leftIfBlock.getCondition(), rightDoWhileBlock.getEvaluation());
		Similarity bodyDiffElse = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getElseExpressions(), rightDoWhileBlock.getBody());
		Similarity simElse = Similarity.add("trans-block", wrongBlockSimilarityElse, evalDiffElse, bodyDiffElse);
		return Similarity.bestOf(simThen, simElse);
	}

	@Override
	public Similarity visit(ForBlock leftForBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "for", "0", "do");
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftForBlock.getEvaluation(), rightDoWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftForBlock.getBody(), rightDoWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(DoWhileBlock leftDoWhileBlock) {
		Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftDoWhileBlock.getEvaluation(), rightDoWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftDoWhileBlock.getBody(), rightDoWhileBlock.getBody());
		return Similarity.add("do", evalDiff, bodyDiff);
	}

	@Override
	public Similarity visit(PlaceholderBlock leftPlaceholderBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "<nothing>", "0", "do");
		Similarity evalDiff = new LeftLeafSimilarity<>(StatementSizer.STATEMENT_SIZER.size(rightDoWhileBlock.getEvaluation()),
			rightDoWhileBlock.getEvaluation());
		Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftPlaceholderBlock.getBody(), rightDoWhileBlock.getBody());
		return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
	}
}
