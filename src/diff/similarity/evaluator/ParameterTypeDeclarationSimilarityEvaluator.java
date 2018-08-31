package diff.similarity.evaluator;

import diff.complexity.ParameterTypeDeclarationSizer;
import diff.similarity.Similarity;
import lexeme.java.tree.ParameterTypeDeclaration;

/**
 * Compares two {@link ParameterTypeDeclaration}s.
 */
public class ParameterTypeDeclarationSimilarityEvaluator extends SimilarityEvaluator<ParameterTypeDeclaration> {

    /** Evaluates {@link ParameterTypeDeclaration}s . */
    public static final ParameterTypeDeclarationSimilarityEvaluator INSTANCE = new ParameterTypeDeclarationSimilarityEvaluator();

    private ParameterTypeDeclarationSimilarityEvaluator() {
        super(ParameterTypeDeclarationSizer.PARAMETER_TYPE__SIZER, "type-param");
    }

    public Similarity eval(ParameterTypeDeclaration params1, ParameterTypeDeclaration params2) {
        Similarity qualSim = QualifierSimilarityEvaluator.INSTANCE.maximumMatch(params1.getQualifiers(), params2.getQualifiers());
        Similarity nameSim = Similarity.eval(params1.getName(), params2.getName());
        Similarity typeSim = ClassNameSimilarityEvaluator.INSTANCE.eval(params1.getType(), params2.getType());
        return Similarity.add(name, qualSim, typeSim, nameSim);
    }
}
