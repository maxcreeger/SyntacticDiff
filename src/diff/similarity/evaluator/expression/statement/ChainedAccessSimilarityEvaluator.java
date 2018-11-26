package diff.similarity.evaluator.expression.statement;

import java.util.List;

import diff.complexity.expression.statement.ChainedAccessSizer;
import diff.similarity.CompositeSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import lexeme.java.tree.expression.statement.ChainedAccess;
import lexeme.java.tree.expression.statement.MethodInvocation;
import prettyprinting.SimilarityChainingHint;

/**
 * Compares two {@link MethodInvocation}s.
 */
public class ChainedAccessSimilarityEvaluator extends SimilarityEvaluator<ChainedAccess> {

	public static final ChainedAccessSimilarityEvaluator INSTANCE = new ChainedAccessSimilarityEvaluator();

	public static class ChainedAccessSimilarity extends CompositeSimilarity {

		protected ChainedAccessSimilarity(double same, int amount, List<Similarity> chains) {
			super("chain", same, amount);
			String chainer = "";
			for (Similarity elem : chains) {
				super.addOne(elem, SimilarityChainingHint.startWith("").inLineWithSeparator(".").endWith(""));
				chainer = ".";
			}
		}

		public static Similarity build(List<Similarity> chains) {
			if (chains.isEmpty()) {
				throw new UnsupportedOperationException("cannot chain nothing ???"); // ignore
			}
			double same = 0;
			int amount = 0;
			for (Similarity similarity : chains) {
				if (similarity.isEmpty()) {
					continue; // ignore
				}
				same += similarity.getSame();
				amount += similarity.getAmount();
			}
			return new ChainedAccessSimilarity(same, amount, chains);
		}

		@Override
		public <T> T accept(SimilarityVisitor<T> visitor) {
			return visitor.visit(this);
		}

	}

	private ChainedAccessSimilarityEvaluator() {
		super(ChainedAccessSizer.CHAINED_ACCESS_SIZER, "chain");
	}

	@Override
	public Similarity eval(ChainedAccess varA, ChainedAccess varB) {
		List<Similarity> chains = StatementSimilarityEvaluator.INSTANCE.strictOrderList(varA.getStatements(), varB.getStatements());
		return ChainedAccessSimilarity.build(chains);
	}
}
