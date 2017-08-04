package diff.similarity.evaluator.expression.statement;

import diff.complexity.expression.statement.VariableReferenceSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import parser.syntaxtree.expression.statement.VariableReference;

/**
 * Compares two {@link VariableReference}s.
 */
public class VariableReferenceSimilarityEvaluator extends SimilarityEvaluator<VariableReference> {

    /** Instance */
    public static final VariableReferenceSimilarityEvaluator INSTANCE = new VariableReferenceSimilarityEvaluator();

    private VariableReferenceSimilarityEvaluator() {
        super(VariableReferenceSizer.VARIABLE_REFERENCE_SIZER, "ref");
    }

    public Similarity eval(VariableReference varA, VariableReference varB) {
        return Similarity.eval(varA.getVariableName(), varB.getVariableName());
    }
}
