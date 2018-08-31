package diff.similarity.evaluator.expression.statement.operator.unary;

import diff.complexity.expression.statement.operator.UnaryOperatorSizer;
import diff.similarity.ExpressionSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.operators.unary.UnaryOperator;

/**
 * Compares two {@link UnaryOperator}s.
 */
public class UnaryOperatorSimilarityEvaluator extends SimilarityEvaluator<UnaryOperator> {

    /** Instance */
    public static final UnaryOperatorSimilarityEvaluator INSTANCE = new UnaryOperatorSimilarityEvaluator();

    private UnaryOperatorSimilarityEvaluator() {
        super(UnaryOperatorSizer.UNARY_OPERATOR_SIZER, "unary");
    }

    public Similarity eval(UnaryOperator operator1, UnaryOperator operator2) {
        boolean sameOperator = operator1.getClass().equals(operator2.getClass());
        Similarity operatorSim = new ExpressionSimilarity<>(sameOperator ? 1 : 0, 1, operator1, operator2);
        Statement statement1 = operator1.getTargetedStatement();
        Statement statement2 = operator2.getTargetedStatement();
        Similarity statementSim = ExpressionSimilarityEvaluator.INSTANCE.eval(statement1, statement2);
        return Similarity.add("unary", operatorSim, statementSim);
    }
}
