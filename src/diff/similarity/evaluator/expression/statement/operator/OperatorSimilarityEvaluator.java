package diff.similarity.evaluator.expression.statement.operator;

import diff.complexity.expression.statement.operator.OperatorSizer;
import diff.similarity.ExpressionSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import lexeme.java.tree.expression.statement.operators.Operator;
import lexeme.java.tree.expression.statement.operators.OperatorVisitor;
import lexeme.java.tree.expression.statement.operators.binary.BinaryOperator;
import lexeme.java.tree.expression.statement.operators.unary.UnaryOperator;

/**
 * Compares two {@link Operator}s.
 */
public class OperatorSimilarityEvaluator extends SimilarityEvaluator<Operator>
        implements
            OperatorVisitor<DualOperatorComparator<? extends Operator>> {

    /** Instance */
    public static final OperatorSimilarityEvaluator INSTANCE = new OperatorSimilarityEvaluator();

    private OperatorSimilarityEvaluator() {
        super(OperatorSizer.OPERATOR_SIZER, "operator");
    }

    public Similarity eval(Operator operator1, Operator operator2) {
        if (operator1.getClass().equals(operator2.getClass())) {
            DualOperatorComparator<? extends Operator> dualComparator = operator2.acceptOperatorVisitor(this);
            return operator1.acceptOperatorVisitor(dualComparator);
        } else {
            return new ExpressionSimilarity<>(0, OperatorSizer.OPERATOR_SIZER.size(operator1, operator2), operator1, operator2);
        }
    }

    @Override
    public DualOperatorComparator<UnaryOperator> visit(UnaryOperator unaryOperator) {
        return new DualOperatorComparator<UnaryOperator>(unaryOperator);
    }

    @Override
    public DualOperatorComparator<BinaryOperator> visit(BinaryOperator binaryOperator) {
        return new DualOperatorComparator<BinaryOperator>(binaryOperator);
    }
}
