package diff.similarity.evaluator.expression.statement.primitivetypes;

import diff.complexity.expression.statement.primitivetypes.PrimitiveValueSizer;
import diff.similarity.ExpressionSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import lexeme.java.tree.expression.statement.primitivetypes.BooleanValue;
import lexeme.java.tree.expression.statement.primitivetypes.CharValue;
import lexeme.java.tree.expression.statement.primitivetypes.DoubleValue;
import lexeme.java.tree.expression.statement.primitivetypes.IntegerValue;
import lexeme.java.tree.expression.statement.primitivetypes.NullValue;
import lexeme.java.tree.expression.statement.primitivetypes.PrimitiveValue;
import lexeme.java.tree.expression.statement.primitivetypes.PrimitiveVisitor;
import lexeme.java.tree.expression.statement.primitivetypes.StringValue;

/**
 * Compares two {@link PrimitiveValue}s.
 */
public class PrimitiveValueSimilarityEvaluator extends SimilarityEvaluator<PrimitiveValue>
        implements
            PrimitiveVisitor<DualPrimitiveValueComparator<? extends PrimitiveValue>> {

    /** Instance */
    public static final PrimitiveValueSimilarityEvaluator INSTANCE = new PrimitiveValueSimilarityEvaluator();

    private PrimitiveValueSimilarityEvaluator() {
        super(PrimitiveValueSizer.PRIMITIVE_VALUE_SIZER, "primitive");
    }

    public Similarity eval(PrimitiveValue val1, PrimitiveValue val2) {
        if (val1.getClass().equals(val2.getClass())) {
            DualPrimitiveValueComparator<? extends PrimitiveValue> dualComparator = val2.visit(this);
            return val1.visit(dualComparator);
        } else {
            return new ExpressionSimilarity<>(0, PrimitiveValueSizer.PRIMITIVE_VALUE_SIZER.size(val1, val2), val1, val2);
        }
    }

    @Override
    public DualPrimitiveValueComparator<StringValue> visit(StringValue stringValue) {
        return new DualPrimitiveValueComparator<StringValue>(stringValue);
    }

    @Override
    public DualPrimitiveValueComparator<NullValue> visit(NullValue nullValue) {
        return new DualPrimitiveValueComparator<NullValue>(nullValue);
    }

    @Override
    public DualPrimitiveValueComparator<IntegerValue> visit(IntegerValue integerValue) {
        return new DualPrimitiveValueComparator<IntegerValue>(integerValue);
    }

    @Override
    public DualPrimitiveValueComparator<DoubleValue> visit(DoubleValue doubleValue) {
        return new DualPrimitiveValueComparator<DoubleValue>(doubleValue);
    }

    @Override
    public DualPrimitiveValueComparator<CharValue> visit(CharValue charValue) {
        return new DualPrimitiveValueComparator<CharValue>(charValue);
    }

    @Override
    public DualPrimitiveValueComparator<BooleanValue> visit(BooleanValue booleanValue) {
        return new DualPrimitiveValueComparator<BooleanValue>(booleanValue);
    }

}
