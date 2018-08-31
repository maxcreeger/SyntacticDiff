package diff.similarity.evaluator;

import diff.complexity.ParameterPassingSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.statement.StatementSimilarityEvaluator;
import lexeme.java.tree.ParameterPassing;

/**
 * Compares two {@link ParameterPassing}s.
 */

public class ParameterPassingSimilarityEvaluator extends SimilarityEvaluator<ParameterPassing> {

    /** Evaluates {@link ParameterPassing}s . */
    public static final ParameterPassingSimilarityEvaluator INSTANCE = new ParameterPassingSimilarityEvaluator();

    private ParameterPassingSimilarityEvaluator() {
        super(ParameterPassingSizer.PARAMETERS_PASSING_SIZER, "param");
    }

    public Similarity eval(ParameterPassing params1, ParameterPassing params2) {
        return Similarity.add(name, StatementSimilarityEvaluator.INSTANCE.orderedEval(params1.getParameters(), params2.getParameters()));
    }
}
