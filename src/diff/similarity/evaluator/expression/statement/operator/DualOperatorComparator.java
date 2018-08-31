package diff.similarity.evaluator.expression.statement.operator;

import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.statement.operator.binary.BinaryOperatorSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.operator.unary.UnaryOperatorSimilarityEvaluator;
import lexeme.java.tree.expression.statement.operators.Operator;
import lexeme.java.tree.expression.statement.operators.OperatorVisitor;
import lexeme.java.tree.expression.statement.operators.binary.BinaryOperator;
import lexeme.java.tree.expression.statement.operators.unary.UnaryOperator;
import lombok.AllArgsConstructor;

/**
 * compares two sub-types of {@link Operator}s together. They must be of the same sub-type
 *
 * @param <T> the common sub-type
 */
@AllArgsConstructor
public class DualOperatorComparator<T extends Operator> implements OperatorVisitor<Similarity> {

    private final T operator2;

    @Override
    public Similarity visit(UnaryOperator unaryOperator1) {
        return UnaryOperatorSimilarityEvaluator.INSTANCE.eval(unaryOperator1, (UnaryOperator) operator2);
    }

    @Override
    public Similarity visit(BinaryOperator binaryOperator1) {
        return BinaryOperatorSimilarityEvaluator.INSTANCE.eval(binaryOperator1, (BinaryOperator) operator2);
    }
}
