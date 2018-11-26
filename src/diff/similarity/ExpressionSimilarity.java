package diff.similarity;

import java.util.ArrayList;
import java.util.List;

import lexeme.java.tree.expression.Expression;

/**
 * Similarity between two expressions.
 *
 * @param <T>
 *            the Type of the {@link Expression}
 */
public class ExpressionSimilarity<T extends Expression> extends LeafSimilarity<T> {

	protected final Expression exp1;
	protected final Expression exp2;

	/**
	 * Builds a comparison between two {@link Expression}, using the known
	 * similarity metric and size metric.
	 * 
	 * @param same
	 *            the similarity
	 * @param amount
	 *            the size of the object
	 * @param obj1
	 *            the left object
	 * @param obj2
	 *            the right object
	 */
	public ExpressionSimilarity(double same, int amount, T obj1, T obj2) {
		super("expr", same, amount, obj1, obj2);
		exp1 = obj1;
		exp2 = obj2;
	}

	@Override
	public List<String[]> show(String prefix) {
		List<String> leftShow = exp1.fullBreakdown(prefix);
		List<String> rightShow = exp2.fullBreakdown(prefix);
		List<String[]> list = new ArrayList<>();
		int i = 0;
		int j = 0;
		while (i < leftShow.size() || j < rightShow.size()) {
			String leftStr = i < leftShow.size() ? leftShow.get(i++) : "";
			String rightStr = j < rightShow.size() ? rightShow.get(j++) : "";
			list.add(new String[] { leftStr, Double.toString(similarity()), rightStr });
		}
		return list;
	}
}
