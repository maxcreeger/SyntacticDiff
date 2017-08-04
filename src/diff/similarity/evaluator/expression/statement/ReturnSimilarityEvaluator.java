package diff.similarity.evaluator.expression.statement;

import diff.complexity.expression.statement.ReturnSizer;
import diff.complexity.expression.statement.StatementSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import parser.syntaxtree.expression.statement.Return;
import parser.syntaxtree.expression.statement.Statement;

/**
 * Compares two {@link Return}s.
 */
public class ReturnSimilarityEvaluator extends SimilarityEvaluator<Return> {

    /** Instance */
    public static final ReturnSimilarityEvaluator INSTANCE = new ReturnSimilarityEvaluator();

    private static final Similarity RETURN_SIMILARITY = new SimpleSimilarity(1, 1, "return", "1", "return");

    private ReturnSimilarityEvaluator() {
        super(ReturnSizer.RETURN_SIZER, "return");
    }

    public Similarity eval(Return varA, Return varB) {
        if (varA.getReturnedValue().isPresent()) {
            final Statement returnValueA = varA.getReturnedValue().get();
            if (varB.getReturnedValue().isPresent()) {
                final Statement returnValueB = varB.getReturnedValue().get();
                Similarity statementSim = StatementSimilarityEvaluator.INSTANCE.eval(returnValueA, returnValueB);
                return Similarity.add(name, RETURN_SIMILARITY, statementSim);
            } else {
                return new LeftLeafSimilarity<>(StatementSizer.STATEMENT_SIZER.size(returnValueA), returnValueA);
            }
        } else {
            if (varB.getReturnedValue().isPresent()) {
                final Statement returnValueB = varB.getReturnedValue().get();
                return new RightLeafSimilarity<>(StatementSizer.STATEMENT_SIZER.size(varB.getReturnedValue().get()), returnValueB);
            } else {
                return RETURN_SIMILARITY;
            }
        }
    }
}
