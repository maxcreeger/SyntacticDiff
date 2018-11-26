package diff.complexity;

import java.util.List;

import lexeme.java.tree.expression.blocks.trycatchfinally.TryBlock;
import tokenizer.CodeLocator.CodeLocation;

public interface Showable {

	/**
	 * Shows the contents in most exploded form possible, using new lines at
	 * every step.
	 * 
	 * @param prefix
	 * @return the breakdown, line by line
	 */
	List<String> fullBreakdown(String prefix);

	/**
	 * Shows the contents in native, compact and well-formatted way.
	 * 
	 * @param prefix
	 * @return the content, line by line
	 */
	default List<String> nativeFormat(String prefix) {
		return fullBreakdown(prefix);
	}

	CodeLocation getLocation();

	public static interface ShowableVisitor<R, T> {
		R visit(TryBlock showable, T input);
	}

	public static class ShowableSizer implements ShowableVisitor<Integer, Void> {

		@Override
		public Integer visit(TryBlock tryBlock, Void input) {
			/*
			int max = 0;
			List<VariableDeclaration> withResource = tryBlock.getTryWithResources();
			for (VariableDeclaration variableDeclaration : withResource) {
				max = Math.max(max, variableDeclaration.accept(this, null));
			}
			List<Expression> body = tryBlock.getTryExpressions();
			for (Expression expression : body) {
				max = Math.max(max, expression.accept(this, null));
			}
			return max;*/
			return null;
		}

	}
}
