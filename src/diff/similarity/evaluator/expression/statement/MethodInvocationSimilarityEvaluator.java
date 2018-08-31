package diff.similarity.evaluator.expression.statement;

import diff.complexity.expression.statement.MethodInvocationSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.ParameterPassingSimilarityEvaluator;
import diff.similarity.evaluator.SimilarityEvaluator;
import lexeme.java.tree.expression.statement.MethodInvocation;

/**
 * Compares two {@link MethodInvocation}s.
 */
public class MethodInvocationSimilarityEvaluator extends SimilarityEvaluator<MethodInvocation> {

    /** Instance */
    public static final MethodInvocationSimilarityEvaluator INSTANCE = new MethodInvocationSimilarityEvaluator();

    private MethodInvocationSimilarityEvaluator() {
        super(MethodInvocationSizer.METHOD_INVOCATION_SIZER, "invoke");
    }

    public Similarity eval(MethodInvocation varA, MethodInvocation varB) {
        Similarity methodNameSim = Similarity.eval(varA.getMethodName(), varB.getMethodName());
        Similarity parameterSim = ParameterPassingSimilarityEvaluator.INSTANCE.eval(varA.getArguments(), varB.getArguments());
        return Similarity.add(name, methodNameSim, parameterSim);
    }
}
