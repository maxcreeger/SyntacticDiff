package diff.similarity.evaluator.expression.statement;

import diff.complexity.expression.statement.NewInstanceSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.ClassNameSimilarityEvaluator;
import diff.similarity.evaluator.ParameterPassingSimilarityEvaluator;
import diff.similarity.evaluator.SimilarityEvaluator;
import parser.syntaxtree.expression.statement.NewInstance;

/**
 * Compares two {@link NewInstance}s.
 */
public class NewInstanceSimilarityEvaluator extends SimilarityEvaluator<NewInstance> {

    /** Instance */
    public static final NewInstanceSimilarityEvaluator INSTANCE = new NewInstanceSimilarityEvaluator();

    private NewInstanceSimilarityEvaluator() {
        super(NewInstanceSizer.NEW_INSTANCE_SIZER, "new");
    }

    public Similarity eval(NewInstance varA, NewInstance varB) {
        Similarity classNameSim = ClassNameSimilarityEvaluator.INSTANCE.eval(varA.getClassName(), varB.getClassName());
        Similarity argumentSim = ParameterPassingSimilarityEvaluator.INSTANCE.eval(varA.getConstructorArguments(), varB.getConstructorArguments());
        return Similarity.add(name, classNameSim, argumentSim);
    }
}
