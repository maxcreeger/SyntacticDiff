package diff.similarity.evaluator.expression.blocks;

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

/** Compares a {@link IfBlock} to any other {@link AbstractBlock} to produce a {@link Similarity}. */
@AllArgsConstructor
@Getter
public class IfBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

    IfBlock rightIfBlock;

    @Override
    public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
		return TryCatchFinallyBlockSimilarityEvaluator.encloseRight(leftTryCatchFinallyBlock, rightIfBlock, new SimpleSimilarity(0, 1, "trycatchfinally", "0",
				"if"));
    }

    @Override
    public Similarity visit(WhileBlock leftWhileBlock) {
        Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "while", "0", "if");
		Similarity condition = StatementSimilarityEvaluator.INSTANCE.eval(leftWhileBlock.getEvaluation(), rightIfBlock.getCondition());
		// Attempt to match the "while body" with "Then Body"
		Similarity whileThen = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftWhileBlock.getBody(), rightIfBlock.getThenExpressions());
		Similarity nothingElse = RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(rightIfBlock.getElseExpressions()),
                    rightIfBlock.getElseExpressions());
		Similarity whileVersusThen = Similarity.add("trans-block", wrongBlockSimilarity, condition, whileThen, nothingElse);

		// Attempt to match the "while body" with "else body"
		Similarity nothingThen = RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(rightIfBlock.getThenExpressions()),
				rightIfBlock.getThenExpressions());
		Similarity whileElse = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftWhileBlock.getBody(), rightIfBlock.getElseExpressions());
		Similarity whileVersusElse = Similarity.add("trans-block", wrongBlockSimilarity, condition, nothingThen, whileElse);
		// Return
		return Similarity.bestOf(whileVersusThen, whileVersusElse);
    }

    @Override
    public Similarity visit(IfBlock leftIfBlock) {
        Similarity condDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftIfBlock.getCondition(), rightIfBlock.getCondition());
        Similarity thenDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getThenExpressions(), rightIfBlock.getThenExpressions());
        Similarity elseDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getElseExpressions(), rightIfBlock.getElseExpressions());
        return Similarity.add("if", condDiff, thenDiff, elseDiff);
    }

    @Override
    public Similarity visit(ForBlock leftForBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "for", "0", "if");
		Similarity init = ExpressionSimilarityEvaluator.INSTANCE.eval(leftForBlock.getInitialisation(), rightIfBlock.getCondition());
		Similarity iter = ExpressionSimilarityEvaluator.INSTANCE.eval(leftForBlock.getIteration(), rightIfBlock.getCondition());
		Similarity condition = ExpressionSimilarityEvaluator.INSTANCE.eval(leftForBlock.getEvaluation(), rightIfBlock.getCondition());
		// Attempt to match the "while body" with "Then Body"
		Similarity whileThen = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftForBlock.getBody(), rightIfBlock.getThenExpressions());
		Similarity nothingElse = RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(rightIfBlock.getElseExpressions()),
				rightIfBlock.getElseExpressions());
		Similarity whileVersusThen = Similarity.add("trans-block", wrongBlockSimilarity, init, iter, condition, whileThen, nothingElse);

		// Attempt to match the "while body" with "else body"
		Similarity nothingThen = RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(rightIfBlock.getThenExpressions()),
				rightIfBlock.getThenExpressions());
		Similarity whileElse = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftForBlock.getBody(), rightIfBlock.getElseExpressions());
		Similarity whileVersusElse = Similarity.add("trans-block", wrongBlockSimilarity, init, iter, condition, nothingThen, whileElse);
		// Return
		return Similarity.bestOf(whileVersusThen, whileVersusElse);
    }

    @Override
    public Similarity visit(DoWhileBlock leftDoWhileBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "do", "0", "if");
		// Attempt to match the "while body" with "Then Body"
		Similarity condition = StatementSimilarityEvaluator.INSTANCE.eval(leftDoWhileBlock.getEvaluation(), rightIfBlock.getCondition());
		Similarity whileThen = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftDoWhileBlock.getBody(), rightIfBlock.getThenExpressions());
		Similarity nothingElse = RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(rightIfBlock.getElseExpressions()),
				rightIfBlock.getElseExpressions());
		Similarity whileVersusThen = Similarity.add("trans-block", wrongBlockSimilarity, condition, whileThen, nothingElse);

		// Attempt to match the "while body" with "else body"
		Similarity nothingThen = RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(rightIfBlock.getThenExpressions()),
				rightIfBlock.getThenExpressions());
		Similarity whileElse = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftDoWhileBlock.getBody(), rightIfBlock.getElseExpressions());
		Similarity whileVersusElse = Similarity.add("trans-block", wrongBlockSimilarity, condition, nothingThen, whileElse);
		// Return
		return Similarity.bestOf(whileVersusThen, whileVersusElse);
	}

	@Override
	public Similarity visit(PlaceholderBlock leftPlaceholderBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "<nothing>", "0", "if");
		// Attempt to match the "while body" with "Then Body"
		Similarity whileThen = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftPlaceholderBlock.getBody(), rightIfBlock.getThenExpressions());
		Similarity nothingElse = RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(rightIfBlock.getElseExpressions()),
				rightIfBlock.getElseExpressions());
		Similarity whileVersusThen = Similarity.add("trans-block", wrongBlockSimilarity, whileThen, nothingElse);

		// Attempt to match the "while body" with "else body"
		Similarity nothingThen = RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(rightIfBlock.getThenExpressions()),
				rightIfBlock.getThenExpressions());
		Similarity whileElse = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftPlaceholderBlock.getBody(), rightIfBlock.getElseExpressions());
		Similarity whileVersusElse = Similarity.add("trans-block", wrongBlockSimilarity, nothingThen, whileElse);
		// Return
		return Similarity.bestOf(whileVersusThen, whileVersusElse);
    }
}
