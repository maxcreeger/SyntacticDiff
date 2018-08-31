package diff.similarity.evaluator.expression.statement.operator.binary;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.expression.statement.operator.BinaryOperatorSizer;
import diff.similarity.LeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.operators.binary.BinaryOperator;

/**
 * Compares two {@link BinaryOperator}s.
 */
public class BinaryOperatorSimilarityEvaluator extends SimilarityEvaluator<BinaryOperator> {

    /** Instance */
    public static final BinaryOperatorSimilarityEvaluator INSTANCE = new BinaryOperatorSimilarityEvaluator();

    private BinaryOperatorSimilarityEvaluator() {
        super(BinaryOperatorSizer.BINARY_OPERATOR_SIZER, "binary");
    }

    public Similarity eval(BinaryOperator operator1, BinaryOperator operator2) {
        boolean sameOperator = operator1.getClass().equals(operator2.getClass());
        Similarity operatorSim = new LeafSimilarity<BinaryOperator>(sameOperator ? 1 : 0, 1, operator1, operator2) {

            @Override
            public List<String[]> show(String prefix) {
                List<String[]> list = new ArrayList<>();
                list.add(
                        new String[] {prefix + operator1.getOperatorSymbol(), Double.toString(similarity()), prefix + operator2.getOperatorSymbol()});
                return list;
            }

        };
        Statement lhs1 = operator1.getLeftHandSide();
        Statement lhs2 = operator2.getLeftHandSide();
        Similarity lhsSim = ExpressionSimilarityEvaluator.INSTANCE.eval(lhs1, lhs2);
        Statement rhs1 = operator1.getRightHandSide();
        Statement rhs2 = operator2.getRightHandSide();
        Similarity rhsSim = ExpressionSimilarityEvaluator.INSTANCE.eval(rhs1, rhs2);
        return Similarity.add(name, operatorSim, lhsSim, rhsSim);
    }
}
