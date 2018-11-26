package diff.similarity.evaluator;

import diff.complexity.ConstructorSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import lexeme.java.tree.Constructor;

public class ConstructorSimilarityEvaluator extends SimilarityEvaluator<Constructor> {
	/** Instance. */
	public static final ConstructorSimilarityEvaluator INSTANCE = new ConstructorSimilarityEvaluator();

	private ConstructorSimilarityEvaluator() {
		super(ConstructorSizer.CONSTRUCTOR_SIZER, "constructor");
	}

	@Override
	public Similarity eval(Constructor ctorA, Constructor ctorB) {
		Similarity simQualifiers = QualifierSimilarityEvaluator.INSTANCE.maximumMatch(ctorA.getQualifiers(), ctorB.getQualifiers());
		Similarity simName = Similarity.eval(ctorA.getName(), ctorB.getName());
		Similarity simParameters = ParameterTypeDeclarationSimilarityEvaluator.INSTANCE.compareWithGaps(ctorA.getParameters(), ctorB.getParameters());
		Similarity simBody = ExpressionSimilarityEvaluator.INSTANCE.compareWithGaps(ctorA.getExpressions(), ctorB.getExpressions());
		return Similarity.add("constructor", simQualifiers, simName, simParameters, simBody);
	}

}
