package diff.similarity.evaluator.expression.statement;

import diff.complexity.expression.statement.ReturnSizer;
import diff.complexity.expression.statement.StatementSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity.ShowableString;
import diff.similarity.evaluator.SimilarityEvaluator;
import lexeme.java.tree.expression.statement.Return;
import lexeme.java.tree.expression.statement.Statement;

/**
 * Compares two {@link Return}s.
 */
public class ReturnSimilarityEvaluator extends SimilarityEvaluator<Return> {

	/** Instance */
	public static final ReturnSimilarityEvaluator INSTANCE = new ReturnSimilarityEvaluator();

	private ReturnSimilarityEvaluator() {
		super(ReturnSizer.RETURN_SIZER, "return");
	}

	@Override
	public Similarity eval(Return varA, Return varB) {
		if (varA.getReturnedValue().isPresent()) {
			final Statement returnValueA = varA.getReturnedValue().get();
			if (varB.getReturnedValue().isPresent()) {
				final Statement returnValueB = varB.getReturnedValue().get();
				Similarity statementSim = StatementSimilarityEvaluator.INSTANCE.eval(returnValueA, returnValueB);
				Similarity keywordSim = Similarity.eval(varA.getReturnKeyword(), varB.getReturnKeyword());
				return Similarity.add(name, keywordSim, statementSim);
			} else {
				return new LeftLeafSimilarity<>(StatementSizer.STATEMENT_SIZER.size(returnValueA), returnValueA);
			}
		} else {
			if (varB.getReturnedValue().isPresent()) {
				final Statement returnValueB = varB.getReturnedValue().get();
				return new RightLeafSimilarity<>(StatementSizer.STATEMENT_SIZER.size(varB.getReturnedValue().get()), returnValueB);
			} else {
				return Similarity.eval(new ShowableString("return", varA.getLocation()), new ShowableString("return", varB.getLocation()));
			}
		}
	}
}
