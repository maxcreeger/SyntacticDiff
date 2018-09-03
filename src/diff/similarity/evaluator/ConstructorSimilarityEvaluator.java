package diff.similarity.evaluator;

import lexeme.java.tree.Constructor;
import diff.complexity.ConstructorSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;

public class ConstructorSimilarityEvaluator extends SimilarityEvaluator<Constructor> {
	/** Instance. */
	public static final ConstructorSimilarityEvaluator INSTANCE = new ConstructorSimilarityEvaluator();

	private ConstructorSimilarityEvaluator() {
		super(ConstructorSizer.CONSTRUCTOR_SIZER, "constructor");
	}

	@Override
	public Similarity eval(Constructor ctorA, Constructor ctorB) {
		Similarity simQualifiers = QualifierSimilarityEvaluator.INSTANCE.maximumMatch(ctorA.getQualifiers(), ctorB.getQualifiers());
		Similarity simName = Similarity.eval(ctorA.getSurroundingClassName(), ctorB.getSurroundingClassName());
		Similarity simParameters = ParameterTypeDeclarationSimilarityEvaluator.INSTANCE.orderedEval(ctorA.getParameters(), ctorB.getParameters());
		Similarity simBody = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(ctorA.getExpressions(), ctorB.getExpressions());
		return Similarity.add("constructor", simQualifiers, simName, simParameters, simBody);
	}

}
