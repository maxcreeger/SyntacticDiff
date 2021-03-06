package lexeme.java.tree.expression.blocks;

import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import diff.similarity.evaluator.expression.blocks.PlaceholderBlock;

/**
 * Visits any {@link AbstractBlock}.
 *
 * @param <T>
 *            the return object type
 */
public interface BlockVisitor<T> {

	/**
	 * Visit a {@link TryCatchFinallyBlock}
	 * 
	 * @param tryCatchFinallyBlock
	 *            the block to visit
	 * @return the returned object
	 */
	T visit(TryCatchFinallyBlock tryCatchFinallyBlock);

	/**
	 * Visit a {@link WhileBlock}
	 * 
	 * @param whileBlock
	 *            the block to visit
	 * @return the returned object
	 */
	T visit(WhileBlock whileBlock);

	/**
	 * Visit a {@link IfBlock}
	 * 
	 * @param ifBlock
	 *            the block to visit
	 * @return the returned object
	 */
	T visit(IfBlock ifBlock);

	/**
	 * Visit a {@link ForBlock}
	 * 
	 * @param forBlock
	 *            the block to visit
	 * @return the returned object
	 */
	T visit(ForBlock forBlock);

	/**
	 * Visit a {@link DoWhileBlock}
	 * 
	 * @param doWhileBlock
	 *            the block to visit
	 * @return the returned object
	 */
	T visit(DoWhileBlock doWhileBlock);

	/**
	 * Visit a {@link PlaceholderBlock}
	 * 
	 * @param placeholderBlock
	 *            the block to visit
	 * @return the returned object
	 */
	T visit(PlaceholderBlock placeholderBlock);

}
