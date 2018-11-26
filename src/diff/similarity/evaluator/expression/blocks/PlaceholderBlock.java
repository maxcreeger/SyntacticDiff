package diff.similarity.evaluator.expression.blocks;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.expression.ExpressionSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.SimpleSimilarity.ShowableString;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.blocks.BlockVisitor;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import lombok.Getter;
import tokenizer.CodeLocator.CodeLocation;

@Getter
public class PlaceholderBlock extends AbstractBlock {

	private final ShowableString placeholderKeyword;
	private final List<Expression> expressions;

	public PlaceholderBlock(List<Expression> expressions, CodeLocation location) {
		super(location);
		this.placeholderKeyword = new ShowableString("<nothing>", location);
		this.expressions = expressions;
	}

	public PlaceholderBlock(Expression expression, CodeLocation location) {
		super(location);
		this.placeholderKeyword = new ShowableString("<nothing>", location);
		this.expressions = new ArrayList<>();
		this.expressions.add(expression);
	}

	public static class PlaceholderBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

		PlaceholderBlock rightPlaceholder;

		public PlaceholderBlockSimilarityEvaluator(AbstractBlock block) {
			this.rightPlaceholder = new PlaceholderBlock(block, block.getLocation());
		}

		public PlaceholderBlockSimilarityEvaluator(PlaceholderBlock block) {
			this.rightPlaceholder = block;
		}

		public PlaceholderBlockSimilarityEvaluator(List<Expression> expressions, CodeLocation location) {
			this.rightPlaceholder = new PlaceholderBlock(expressions, location);
		}

		public PlaceholderBlockSimilarityEvaluator(Expression expression, CodeLocation location) {
			this.rightPlaceholder = new PlaceholderBlock(expression, location);
		}

		@Override
		public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
			return TryCatchFinallyBlockSimilarityEvaluator.encloseRight(leftTryCatchFinallyBlock, rightPlaceholder,
				new SimpleSimilarity(0, 1, leftTryCatchFinallyBlock.getTryBlock().getTryKeyword(), "0", rightPlaceholder.getPlaceholderKeyword()));
		}

		@Override
		public Similarity visit(WhileBlock leftWhileBlock) {
			Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftWhileBlock.getWhileKeyword(), "0", rightPlaceholder.getPlaceholderKeyword());
			Similarity evalDiff = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftWhileBlock.getEvaluation()),
				leftWhileBlock.getEvaluation());
			Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftWhileBlock.getBody(), rightPlaceholder.expressions);
			return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
		}

		@Override
		public Similarity visit(IfBlock leftIfBlock) {
			// Match placeholder with THEN block
			Similarity wrongBlockSimilarityThen = new SimpleSimilarity(0, 1, leftIfBlock.getIfKeyword(), "0", rightPlaceholder.getPlaceholderKeyword());
			Similarity evalDiffThen = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftIfBlock.getCondition()), leftIfBlock.getCondition());
			Similarity bodyDiffThen = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftIfBlock.getBody(), rightPlaceholder.expressions);
			Similarity simThen = Similarity.add("trans-block", wrongBlockSimilarityThen, evalDiffThen, bodyDiffThen);

			// Match placeholder with ELSE block
			if (leftIfBlock.getElseKeyword().isPresent()) {
				Similarity wrongBlockSimilarityElse = new SimpleSimilarity(0, 1, leftIfBlock.getElseKeyword().get(), "0",
					rightPlaceholder.getPlaceholderKeyword());
				Similarity evalDiffElse = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftIfBlock.getCondition()),
					leftIfBlock.getCondition());
				Similarity bodyDiffElse = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftIfBlock.getElseExpressions(),
					rightPlaceholder.expressions);
				Similarity simElse = Similarity.add("trans-block", wrongBlockSimilarityElse, evalDiffElse, bodyDiffElse);
				return Similarity.bestOf(simThen, simElse);
			} else {
				return simThen;
			}
		}

		@Override
		public Similarity visit(ForBlock leftForBlock) {
			Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftForBlock.getForKeyword(), "0", rightPlaceholder.getPlaceholderKeyword());
			Similarity evalDiff = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftForBlock.getEvaluation()), leftForBlock.getEvaluation());
			Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftForBlock.getBody(), rightPlaceholder.expressions);
			return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
		}

		@Override
		public Similarity visit(DoWhileBlock leftDoWhileBlock) {
			Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftDoWhileBlock.getDoKeyword(), "0", rightPlaceholder.getPlaceholderKeyword());
			Similarity evalDiff = new LeftLeafSimilarity<>(ExpressionSizer.EXPRESSION_SIZER.size(leftDoWhileBlock.getEvaluation()),
				leftDoWhileBlock.getEvaluation());
			Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftDoWhileBlock.getBody(), rightPlaceholder.expressions);
			return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
		}

		@Override
		public Similarity visit(PlaceholderBlock leftPlaceholderBlock) {
			return ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftPlaceholderBlock.getBody(), rightPlaceholder.getBody());
		}
	};

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> result = new ArrayList<>();
		result.add(prefix + "<placeholder> {");
		String bodyPrefix = prefix + "~  ";
		for (Expression expression : expressions) {
			result.addAll(expression.fullBreakdown(bodyPrefix));
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
