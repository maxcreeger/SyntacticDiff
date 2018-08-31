package diff.similarity.evaluator.expression;

import diff.complexity.expression.VariableDeclarationSizer;
import diff.complexity.expression.statement.StatementSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.ClassNameSimilarityEvaluator;
import diff.similarity.evaluator.QualifierSimilarityEvaluator;
import diff.similarity.evaluator.SimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.StatementSimilarityEvaluator;
import lexeme.java.tree.expression.VariableDeclaration;

/**
 * Compares two {@link VariableDeclaration}s.
 */
public class VariableDeclarationSimilarityEvaluator extends SimilarityEvaluator<VariableDeclaration> {
    /** Instance. */
    public static final VariableDeclarationSimilarityEvaluator INSTANCE = new VariableDeclarationSimilarityEvaluator();

    private VariableDeclarationSimilarityEvaluator() {
        super(VariableDeclarationSizer.VARIABLE_DECLARATION_SIZER, "var-init");
    }

    public Similarity eval(VariableDeclaration var1, VariableDeclaration var2) {
        Similarity typeSim = ClassNameSimilarityEvaluator.INSTANCE.eval(var1.getType(), var2.getType());
        Similarity nameSim = Similarity.eval(var1.getName(), var2.getName());
        Similarity qualifiersSim = QualifierSimilarityEvaluator.INSTANCE.maximumMatch(var1.getQualifiers(), var2.getQualifiers());
        Similarity assignmentsim = Similarity.eval(var1.getInitialAssignement(), var2.getInitialAssignement(), StatementSimilarityEvaluator.INSTANCE,
                StatementSizer.STATEMENT_SIZER);
        return Similarity.add(name, typeSim, nameSim, qualifiersSim, assignmentsim);
    }
}
