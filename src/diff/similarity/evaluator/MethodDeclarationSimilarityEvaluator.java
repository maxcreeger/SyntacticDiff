package diff.similarity.evaluator;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.MethodDeclarationSizer;
import diff.similarity.CompositeSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import lexeme.java.tree.MethodDeclaration;
import prettyprinting.SimilarityChainingHint;

/**
 * Compares two {@link MethodDeclaration}s.
 */
public class MethodDeclarationSimilarityEvaluator extends SimilarityEvaluator<MethodDeclaration> {

	/** Instance. */
	public static final MethodDeclarationSimilarityEvaluator INSTANCE = new MethodDeclarationSimilarityEvaluator();

	private MethodDeclarationSimilarityEvaluator() {
		super(MethodDeclarationSizer.METHOD_DECLARATION_SIZER, "method");
	}

	@Override
	public Similarity eval(MethodDeclaration methodA, MethodDeclaration methodB) {
		List<Similarity> simQualifiers = QualifierSimilarityEvaluator.INSTANCE.compareWithGapsList(methodA.getQualifiers(), methodB.getQualifiers());
		Similarity simReturnType = ClassNameSimilarityEvaluator.INSTANCE.eval(methodA.getReturnType(), methodB.getReturnType());
		Similarity simName = Similarity.eval(methodA.getName(), methodB.getName());
		List<Similarity> simParameters = ParameterTypeDeclarationSimilarityEvaluator.INSTANCE.compareWithGapsList(methodA.getParameters(),
			methodB.getParameters());
		List<Similarity> simBody = ExpressionSimilarityEvaluator.INSTANCE.compareWithGapsList(methodA.getExpressions(), methodB.getExpressions());
		return MethodDeclarationSimilarity.build(simQualifiers, simReturnType, simName, simParameters, simBody);
	}

	public static class MethodDeclarationSimilarity extends CompositeSimilarity {

		protected MethodDeclarationSimilarity(double same, int amount, List<Similarity> simQualifiers, Similarity simReturnType, Similarity simName,
			List<Similarity> simParameters, List<Similarity> simBody) {
			super("method", same, amount);
			super.addAll(simQualifiers, SimilarityChainingHint.start().inLineWithSeparator(" ").endWith(" "));
			super.addOne(simReturnType, SimilarityChainingHint.startWith(" ").inLine().end());
			if (simParameters.isEmpty()) {
				super.addOne(simName, SimilarityChainingHint.start().inLine().endWith("() {"));
			} else {
				super.addOne(simName, SimilarityChainingHint.start().inLine().end());
				super.addAll(simParameters, SimilarityChainingHint.startWith("(").inLineWithSeparator(",").endWith(") {"));
			}
			super.addAll(simBody, SimilarityChainingHint.start().newLines().endWith("}"));
			if (super.contents.isEmpty()) {
				throw new UnsupportedOperationException("No content inside an aggregate???");
			}
		}

		public static MethodDeclarationSimilarity build(List<Similarity> simQualifiers, Similarity simReturnType, Similarity simName,
			List<Similarity> simParameters, List<Similarity> simBody) {
			List<Similarity> allSims = new ArrayList<>();
			allSims.addAll(simQualifiers);
			allSims.add(simReturnType);
			allSims.add(simName);
			allSims.addAll(simParameters);
			allSims.addAll(simBody);

			double same = 0;
			int amount = 0;
			for (Similarity similarity : allSims) {
				if (similarity.isEmpty()) {
					continue; // ignore
				}
				same += similarity.getSame();
				amount += similarity.getAmount();
			}
			return new MethodDeclarationSimilarity(same, amount, simQualifiers, simReturnType, simName, simParameters, simBody);
		}

		@Override
		public <T> T accept(SimilarityVisitor<T> visitor) {
			return visitor.visit(this);
		}
	}
}
