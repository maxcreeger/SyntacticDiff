package diff.similarity.evaluator.expression.blocks;

import java.util.ArrayList;
import java.util.List;

import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.blocks.BlockVisitor;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import diff.complexity.expression.ExpressionSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;

public class PlaceholderBlock extends AbstractBlock {

	List<Expression> expressions;

	public PlaceholderBlock(List<Expression> expressions) {
		this.expressions = expressions;
	}

	public PlaceholderBlock(Expression expression) {
		this.expressions = new ArrayList<>();
		this.expressions.add(expression);
	}

	public static class PlaceholderBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

		PlaceholderBlock rightPlaceholder;

		public PlaceholderBlockSimilarityEvaluator(PlaceholderBlock block) {
			this.rightPlaceholder = block;
		}

		public PlaceholderBlockSimilarityEvaluator(List<Expression> expressions) {
			this.rightPlaceholder = new PlaceholderBlock(expressions);
		}

		public PlaceholderBlockSimilarityEvaluator(Expression expression) {
			this.rightPlaceholder = new PlaceholderBlock(expression);
		}

		@Override
		public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
			return TryCatchFinallyBlockSimilarityEvaluator.encloseRight(leftTryCatchFinallyBlock, rightPlaceholder, new SimpleSimilarity(0, 1,
					"trycatchfinally", "0", "do"));
		}

		@Override
		public Similarity visit(WhileBlock leftWhileBlock) {
			Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "while", "0", "<nothing>");
			Similarity evalDiff = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftWhileBlock.getEvaluation()),
					leftWhileBlock.getEvaluation());
			Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftWhileBlock.getBody(), rightPlaceholder.expressions);
			return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
		}

		@Override
		public Similarity visit(IfBlock leftIfBlock) {
			// Match placeholder with THEN block
			Similarity wrongBlockSimilarityThen = new SimpleSimilarity(0, 1, "if-then", "0", "<nothing>");
			Similarity evalDiffThen = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftIfBlock.getCondition()), leftIfBlock.getCondition());
			Similarity bodyDiffThen = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getBody(), rightPlaceholder.expressions);
			Similarity simThen = Similarity.add("trans-block", wrongBlockSimilarityThen, evalDiffThen, bodyDiffThen);

			// Match placeholder with ELSE block
			Similarity wrongBlockSimilarityElse = new SimpleSimilarity(0, 1, "if-else", "0", "<nothing>");
			Similarity evalDiffElse = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftIfBlock.getCondition()), leftIfBlock.getCondition());
			Similarity bodyDiffElse = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getElseExpressions(), rightPlaceholder.expressions);
			Similarity simElse = Similarity.add("trans-block", wrongBlockSimilarityElse, evalDiffElse, bodyDiffElse);
			return Similarity.bestOf(simThen, simElse);
		}

		@Override
		public Similarity visit(ForBlock leftForBlock) {
			Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "for", "0", "<nothing>");
			Similarity evalDiff = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftForBlock.getEvaluation()), leftForBlock.getEvaluation());
			Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftForBlock.getBody(), rightPlaceholder.expressions);
			return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
		}

		@Override
		public Similarity visit(DoWhileBlock leftDoWhileBlock) {
			Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "for", "0", "<nothing>");
			Similarity evalDiff = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftDoWhileBlock.getEvaluation()),
					leftDoWhileBlock.getEvaluation());
			Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftDoWhileBlock.getBody(), rightPlaceholder.expressions);
			return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
		}

		@Override
		public Similarity visit(PlaceholderBlock leftPlaceholderBlock) {
			return ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftPlaceholderBlock.getBody(), rightPlaceholder.getBody());
		}
	};

	@Override
	public List<String> show(String prefix) {
		List<String> result = new ArrayList<>();
		result.add(prefix + "<placeholder> {");
		String bodyPrefix = prefix + "~  ";
		for (Expression expression : expressions) {
			result.addAll(expression.show(bodyPrefix));
		}
		result.add(prefix + "}");
		return result;
	}

	@Override
	public <T> T acceptBlockVisitor(BlockVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public List<Expression> getBody() {
		return expressions;
	}
}
