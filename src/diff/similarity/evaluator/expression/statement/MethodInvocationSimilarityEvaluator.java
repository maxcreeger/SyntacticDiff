package diff.similarity.evaluator.expression.statement;

import diff.complexity.expression.statement.MethodInvocationSizer;
import diff.similarity.CompositeSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.ParameterPassingSimilarityEvaluator;
import diff.similarity.evaluator.SimilarityEvaluator;
import lexeme.java.tree.expression.statement.MethodInvocation;
import prettyprinting.SimilarityChainingHint;

/**
 * Compares two {@link MethodInvocation}s.
 */
public class MethodInvocationSimilarityEvaluator extends SimilarityEvaluator<MethodInvocation> {

	/** Instance */
	public static final MethodInvocationSimilarityEvaluator INSTANCE = new MethodInvocationSimilarityEvaluator();

	public static class MethodInvocationSimilarity extends CompositeSimilarity {

		protected MethodInvocationSimilarity(double same, int amount, Similarity methodNameSim, Similarity parameterSim) {
			super("method", same, amount);
			super.addOne(methodNameSim, SimilarityChainingHint.start().inLine().end());
			super.addOne(parameterSim, SimilarityChainingHint.startWith("(").inLineWithSeparator(", ").endWith(")"));
		}

		public static MethodInvocationSimilarity build(Similarity methodNameSim, Similarity parameterSim) {
			Similarity[] allSims = new Similarity[] { methodNameSim, parameterSim };
			double same = 0;
			int amount = 0;
			for (Similarity similarity : allSims) {
				if (similarity.isEmpty()) {
					continue; // ignore
				}
				same += similarity.getSame();
				amount += similarity.getAmount();
			}
			return new MethodInvocationSimilarity(same, amount, methodNameSim, parameterSim);
		}

		@Override
		public <T> T accept(SimilarityVisitor<T> visitor) {
			return visitor.visit(this);
		}

	}

	private MethodInvocationSimilarityEvaluator() {
		super(MethodInvocationSizer.METHOD_INVOCATION_SIZER, "invoke");
	}

	@Override
	public Similarity eval(MethodInvocation varA, MethodInvocation varB) {
		Similarity methodNameSim = Similarity.eval(varA.getMethodName(), varB.getMethodName());
		Similarity parameterSim = ParameterPassingSimilarityEvaluator.INSTANCE.eval(varA.getArguments(), varB.getArguments());
		return MethodInvocationSimilarity.build(methodNameSim, parameterSim);
	}
}
