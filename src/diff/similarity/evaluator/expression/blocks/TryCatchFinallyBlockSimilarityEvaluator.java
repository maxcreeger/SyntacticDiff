package diff.similarity.evaluator.expression.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import diff.complexity.expression.ExpressionSizer;
import diff.complexity.expression.blocks.trycatchfinally.CatchBlockSizer;
import diff.complexity.expression.blocks.trycatchfinally.FinallyBlockSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.NoSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import diff.similarity.evaluator.expression.blocks.trycatchfinally.CatchBlockSimilarityEvaluator;
import diff.similarity.evaluator.expression.blocks.trycatchfinally.FinallyBlockSimilarityEvaluator;
import diff.similarity.evaluator.expression.blocks.trycatchfinally.TryBlockSimilarityEvaluator;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.VariableDeclaration;
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
 * Compares a {@link TryCatchFinallyBlock} to any other {@link AbstractBlock} to
 * produce a {@link Similarity}.
 */
@AllArgsConstructor
@Getter
public class TryCatchFinallyBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

	TryCatchFinallyBlock rightTryCatchFinallyBlock;

	@Override
	public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
		Similarity trySim = TryBlockSimilarityEvaluator.INSTANCE.eval(leftTryCatchFinallyBlock.getTryBlock(), rightTryCatchFinallyBlock.getTryBlock());
		Similarity catchSim = CatchBlockSimilarityEvaluator.INSTANCE.compareWithGaps(leftTryCatchFinallyBlock.getCatchBlocks(),
			rightTryCatchFinallyBlock.getCatchBlocks());
		Similarity finallySim = FinallyBlockSimilarityEvaluator.INSTANCE.eval(leftTryCatchFinallyBlock.getFinallyBlock(),
			rightTryCatchFinallyBlock.getFinallyBlock());
		return Similarity.add("TryCatchFinally", trySim, catchSim, finallySim);
	}

	@Override
	public Similarity visit(WhileBlock leftWhileBlock) {
		Similarity wrongBlockSim = new SimpleSimilarity(0, 1, leftWhileBlock.getWhileKeyword(), "0", rightTryCatchFinallyBlock.getTryBlock().getTryKeyword());
		return encloseLeft(leftWhileBlock, wrongBlockSim);
	}

	@Override
	public Similarity visit(IfBlock leftIfBlock) {
		Similarity wrongBlockSim = new SimpleSimilarity(0, 1, leftIfBlock.getIfKeyword(), "0", rightTryCatchFinallyBlock.getTryBlock().getTryKeyword());
		// TODO try 'else'
		return encloseLeft(leftIfBlock, wrongBlockSim);
	}

	@Override
	public Similarity visit(ForBlock leftForBlock) {
		Similarity wrongBlockSim = new SimpleSimilarity(0, 1, leftForBlock.getForKeyword(), "0", rightTryCatchFinallyBlock.getTryBlock().getTryKeyword());
		return encloseLeft(leftForBlock, wrongBlockSim);
	}

	@Override
	public Similarity visit(DoWhileBlock leftDoWhileBlock) {
		Similarity wrongBlockSim = new SimpleSimilarity(0, 1, leftDoWhileBlock.getDoKeyword(), "0", rightTryCatchFinallyBlock.getTryBlock().getTryKeyword());
		return encloseLeft(leftDoWhileBlock, wrongBlockSim);
	}

	@Override
	public Similarity visit(PlaceholderBlock leftPlaceholderBlock) {
		Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, leftPlaceholderBlock.getPlaceholderKeyword(), "0",
			rightTryCatchFinallyBlock.getTryBlock().getTryKeyword());
		return encloseLeft(leftPlaceholderBlock, wrongBlockSimilarity);
	}

	/**
	 * Enclose a left {@link Expression} in this {@link TryCatchFinallyBlock} on
	 * the right.
	 * 
	 * @param leftExpression
	 *            an {@link Expression}
	 * @param wrongBlockSim
	 *            header {@link Similarity} to tell there is a difference (the
	 *            enclosing)
	 * @return a {@link Similarity}
	 */
	public Similarity encloseLeft(Expression leftExpression, Similarity wrongBlockSim) {
		// Right leaf: resources
		List<VariableDeclaration> tryWithResources = rightTryCatchFinallyBlock.getTryBlock().getTryWithResources();
		List<Expression> tryWithResourcesExpressions = new ArrayList<>(tryWithResources);
		Similarity resourceSim = Similarity.add("resources",
			tryWithResourcesExpressions	.stream().map(res -> LeftLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(res), res))
										.collect(Collectors.toList()));
		// Body comparison
		List<Expression> leftWhileInList = new ArrayList<>();
		leftWhileInList.add(leftExpression);
		Similarity bodySim = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftWhileInList,
			rightTryCatchFinallyBlock.getTryBlock().getTryExpressions());
		// Right leaf: catch & finally
		Similarity catchSim = Similarity.add("catch",
			rightTryCatchFinallyBlock	.getCatchBlocks().stream().map(expr -> LeftLeafSimilarity.build(CatchBlockSizer.CATCH_BLOCK_SIZER.size(expr), expr))
										.collect(Collectors.toList()));
		Similarity finallySim = rightTryCatchFinallyBlock.getFinallyBlock().isPresent()
			? new RightLeafSimilarity<>(FinallyBlockSizer.FINALLY_BLOCK_SIZER.size(rightTryCatchFinallyBlock.getFinallyBlock().get()),
				rightTryCatchFinallyBlock.getFinallyBlock().get())
			: new NoSimilarity();
		return Similarity.add("trans-block", wrongBlockSim, resourceSim, bodySim, catchSim, finallySim);
	}

	/**
	 * Enclose a right {@link Expression} in a {@link TryCatchFinallyBlock} on
	 * the left.
	 * 
	 * @param leftTryCatchFinallyBlock
	 *            the left {@link TryCatchFinallyBlock}
	 * @param rightExpression
	 *            an {@link Expression}
	 * @param wrongBlockSim
	 *            header {@link Similarity} to tell there is a difference (the
	 *            enclosing)
	 * @return a {@link Similarity}
	 */
	public static Similarity encloseRight(TryCatchFinallyBlock leftTryCatchFinallyBlock, Expression rightExpression, Similarity wrongBlockSim) {
		// Right leaf: resources
		List<VariableDeclaration> tryWithResources = leftTryCatchFinallyBlock.getTryBlock().getTryWithResources();
		List<Expression> tryWithResourcesExpressions = new ArrayList<>(tryWithResources);
		Similarity resourceSim = Similarity.add("resources",
			tryWithResourcesExpressions	.stream().map(res -> LeftLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(res), res))
										.collect(Collectors.toList()));
		// Body comparison
		List<Expression> rightExpressionInList = new ArrayList<>();
		rightExpressionInList.add(rightExpression);
		Similarity bodySim = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(leftTryCatchFinallyBlock.getTryBlock().getTryExpressions(),
			rightExpressionInList);
		// Right leaf: catch & finally
		Similarity catchSim = Similarity.add("catch",
			leftTryCatchFinallyBlock.getCatchBlocks().stream().map(expr -> LeftLeafSimilarity.build(CatchBlockSizer.CATCH_BLOCK_SIZER.size(expr), expr))
									.collect(Collectors.toList()));
		Similarity finallySim = leftTryCatchFinallyBlock.getFinallyBlock().isPresent()
			? new LeftLeafSimilarity<>(FinallyBlockSizer.FINALLY_BLOCK_SIZER.size(leftTryCatchFinallyBlock.getFinallyBlock().get()),
				leftTryCatchFinallyBlock.getFinallyBlock().get())
			: new NoSimilarity();
		return Similarity.add("trans-block", wrongBlockSim, resourceSim, bodySim, catchSim, finallySim);
	}

}
