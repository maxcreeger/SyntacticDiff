package diff.similarity.evaluator.expression.blocks;

import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.blocks.BlockVisitor;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import lombok.AllArgsConstructor;
import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.blocks.PlaceholderBlock.PlaceholderBlockSimilarityEvaluator;

/**
 * Compares a {@link TryCatchFinallyBlock} to any other {@link AbstractBlock} to
 * produce a {@link Similarity}.
 */
@AllArgsConstructor
public class TransgenderBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

	AbstractBlock leftBlock;

	@Override
	public Similarity visit(TryCatchFinallyBlock rightTryCatchFinallyBlock) {
		Similarity direct = leftBlock.acceptBlockVisitor(new TryCatchFinallyBlockSimilarityEvaluator(rightTryCatchFinallyBlock));
		Similarity rightInPlaceholder = leftBlock.acceptBlockVisitor(new PlaceholderBlockSimilarityEvaluator(rightTryCatchFinallyBlock));
		Similarity leftInPlaceholder = new PlaceholderBlockSimilarityEvaluator(leftBlock).visit(rightTryCatchFinallyBlock);
		return Similarity.bestOf(leftInPlaceholder, direct, rightInPlaceholder);
	}

	@Override
	public Similarity visit(WhileBlock rightWhileBlock) {
		Similarity direct = leftBlock.acceptBlockVisitor(new WhileBlockSimilarityEvaluator(rightWhileBlock));
		Similarity rightInPlaceholder = leftBlock.acceptBlockVisitor(new PlaceholderBlockSimilarityEvaluator(rightWhileBlock));
		Similarity leftInPlaceholder = new PlaceholderBlockSimilarityEvaluator(leftBlock).visit(rightWhileBlock);
		return Similarity.bestOf(leftInPlaceholder, direct, rightInPlaceholder);
	}

	@Override
	public Similarity visit(IfBlock rightIfBlock) {
		Similarity direct = leftBlock.acceptBlockVisitor(new IfBlockSimilarityEvaluator(rightIfBlock));
		Similarity rightInPlaceholder = leftBlock.acceptBlockVisitor(new PlaceholderBlockSimilarityEvaluator(rightIfBlock));
		Similarity leftInPlaceholder = new PlaceholderBlockSimilarityEvaluator(leftBlock).visit(rightIfBlock);
		return Similarity.bestOf(leftInPlaceholder, direct, rightInPlaceholder);
	}

	@Override
	public Similarity visit(ForBlock rightForBlock) {
		Similarity direct = leftBlock.acceptBlockVisitor(new ForBlockSimilarityEvaluator(rightForBlock));
		Similarity rightInPlaceholder = leftBlock.acceptBlockVisitor(new PlaceholderBlockSimilarityEvaluator(rightForBlock));
		Similarity leftInPlaceholder = new PlaceholderBlockSimilarityEvaluator(leftBlock).visit(rightForBlock);
		return Similarity.bestOf(leftInPlaceholder, direct, rightInPlaceholder);
	}

	@Override
	public Similarity visit(DoWhileBlock rightDoWhileBlock) {
		Similarity direct = leftBlock.acceptBlockVisitor(new DoWhileBlockSimilarityEvaluator(rightDoWhileBlock));
		Similarity rightInPlaceholder = leftBlock.acceptBlockVisitor(new PlaceholderBlockSimilarityEvaluator(rightDoWhileBlock));
		Similarity leftInPlaceholder = new PlaceholderBlockSimilarityEvaluator(leftBlock).visit(rightDoWhileBlock);
		return Similarity.bestOf(leftInPlaceholder, direct, rightInPlaceholder);
	}

	@Override
	public Similarity visit(PlaceholderBlock rightPlaceholderBlock) {
		return new PlaceholderBlockSimilarityEvaluator(leftBlock).visit(rightPlaceholderBlock);
	}
}
