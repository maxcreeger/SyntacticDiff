package diff.similarity.evaluator.expression;

import diff.complexity.expression.VariableDeclarationSizer;
import diff.complexity.expression.statement.StatementSizer;
import diff.similarity.CompositeSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.ClassNameSimilarityEvaluator;
import diff.similarity.evaluator.QualifierSimilarityEvaluator;
import diff.similarity.evaluator.SimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.StatementSimilarityEvaluator;
import lexeme.java.tree.expression.VariableDeclaration;
import prettyprinting.SimilarityChainingHint;

/**
 * Compares two {@link VariableDeclaration}s.
 */
public class VariableDeclarationSimilarityEvaluator extends SimilarityEvaluator<VariableDeclaration> {
	/** Instance. */
	public static final VariableDeclarationSimilarityEvaluator INSTANCE = new VariableDeclarationSimilarityEvaluator();

	private VariableDeclarationSimilarityEvaluator() {
		super(VariableDeclarationSizer.VARIABLE_DECLARATION_SIZER, "var-init");
	}

	@Override
	public Similarity eval(VariableDeclaration var1, VariableDeclaration var2) {
		Similarity qualifiersSim = QualifierSimilarityEvaluator.INSTANCE.maximumMatch(var1.getQualifiers(), var2.getQualifiers());
		Similarity typeSim = ClassNameSimilarityEvaluator.INSTANCE.eval(var1.getType(), var2.getType());
		Similarity nameSim = Similarity.eval(var1.getName(), var2.getName());
		Similarity assignmentsim = Similarity.eval(var1.getInitialAssignement(), var2.getInitialAssignement(), StatementSimilarityEvaluator.INSTANCE,
			StatementSizer.STATEMENT_SIZER);
		return VariableDeclarationSimilarity.build(qualifiersSim, typeSim, nameSim, assignmentsim);
	}

	public static class VariableDeclarationSimilarity extends CompositeSimilarity {

		public static VariableDeclarationSimilarity build(Similarity qualifiersSim, Similarity typeSim, Similarity nameSim, Similarity assignmentsim) {
			Similarity[] allSims = new Similarity[] { qualifiersSim, typeSim, nameSim, assignmentsim };
			double same = 0;
			int amount = 0;
			for (Similarity similarity : allSims) {
				if (similarity.isEmpty()) {
					continue; // ignore
				}
				same += similarity.getSame();
				amount += similarity.getAmount();
			}
			return new VariableDeclarationSimilarity(same, amount, qualifiersSim, typeSim, nameSim, assignmentsim);
		}

		protected VariableDeclarationSimilarity(double same, int amount, Similarity qualifiersSim, Similarity typeSim, Similarity nameSim,
			Similarity assignmentsim) {
			super("var-init", same, amount);
			super.addOne(qualifiersSim, SimilarityChainingHint.start().inLineWithSeparator(" ").endWith(" "));
			super.addOne(typeSim, SimilarityChainingHint.start().inLine().endWith(" "));
			super.addOne(nameSim, SimilarityChainingHint.start().inLine().end());
			super.addOne(assignmentsim, SimilarityChainingHint.startWith(" = ").inLine().end());
		}

		@Override
		public <T> T accept(SimilarityVisitor<T> visitor) {
			return visitor.visit(this);
		}
	}
}
