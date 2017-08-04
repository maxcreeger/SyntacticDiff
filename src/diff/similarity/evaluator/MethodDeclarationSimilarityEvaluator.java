package diff.similarity.evaluator;

import diff.complexity.MethodDeclarationSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import parser.syntaxtree.MethodDeclaration;

/**
 * Compares two {@link MethodDeclaration}s.
 */
public class MethodDeclarationSimilarityEvaluator extends SimilarityEvaluator<MethodDeclaration> {

    /** Instance. */
    public static final MethodDeclarationSimilarityEvaluator INSTANCE = new MethodDeclarationSimilarityEvaluator();

    private MethodDeclarationSimilarityEvaluator() {
        super(MethodDeclarationSizer.METHOD_DECLARATION_SIZER, "method");
    }

    public Similarity eval(MethodDeclaration methodA, MethodDeclaration methodB) {
        Similarity simQualifiers = QualifierSimilarityEvaluator.INSTANCE.maximumMatch(methodA.getQualifiers(), methodB.getQualifiers());
        Similarity simReturnType = ClassNameSimilarityEvaluator.INSTANCE.eval(methodA.getReturnType(), methodB.getReturnType());
        Similarity simName = Similarity.eval(methodA.getName(), methodB.getName());
        Similarity simParameters = ParameterTypeDeclarationSimilarityEvaluator.INSTANCE.orderedEval(methodA.getParameters(), methodB.getParameters());
        Similarity simBody = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(methodA.getExpressions(), methodB.getExpressions());
        return Similarity.add("method", simQualifiers, simReturnType, simName, simParameters, simBody);
    }
}
