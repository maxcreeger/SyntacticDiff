package diff.similarity.evaluator.expression.statement.primitivetypes;

import diff.similarity.ExpressionSimilarity;
import diff.similarity.Similarity;
import lombok.AllArgsConstructor;
import parser.syntaxtree.expression.statement.primitivetypes.BooleanValue;
import parser.syntaxtree.expression.statement.primitivetypes.CharValue;
import parser.syntaxtree.expression.statement.primitivetypes.DoubleValue;
import parser.syntaxtree.expression.statement.primitivetypes.IntegerValue;
import parser.syntaxtree.expression.statement.primitivetypes.NullValue;
import parser.syntaxtree.expression.statement.primitivetypes.PrimitiveValue;
import parser.syntaxtree.expression.statement.primitivetypes.PrimitiveVisitor;
import parser.syntaxtree.expression.statement.primitivetypes.StringValue;


/**
 * compares two sub-types of {@link PrimitiveValue} together. They must be of the same sub-type
 *
 * @param <T> the common sub-type
 */
@AllArgsConstructor
public class DualPrimitiveValueComparator<T extends PrimitiveValue> implements PrimitiveVisitor<Similarity> {

    private final T val2;

    @Override
    public Similarity visit(StringValue stringValue1) {
        StringValue stringValue2 = (StringValue) val2;
        return Similarity.eval(stringValue1.getStringContent(), stringValue2.getStringContent());
    }

    @Override
    public Similarity visit(NullValue nullValue) {
        return new ExpressionSimilarity<>(1, 1, nullValue, (NullValue) val2);
    }

    @Override
    public Similarity visit(IntegerValue integerValue1) {
        IntegerValue integerValue2 = (IntegerValue) val2;
        boolean sameValue = integerValue1.getDecimalValue() == integerValue2.getDecimalValue() && integerValue1.isLong() == integerValue2.isLong();
        return new ExpressionSimilarity<>(sameValue ? 1 : 0, 1, integerValue1, integerValue2);
    }

    @Override
    public Similarity visit(DoubleValue doubleValue1) {
        DoubleValue doubleValue2 = (DoubleValue) val2;
        boolean sameValue = doubleValue1.getDoubleValue() == doubleValue2.getDoubleValue() && doubleValue1.isDouble() == doubleValue2.isDouble();
        return new ExpressionSimilarity<>(sameValue ? 1 : 0, 1, doubleValue1, doubleValue2);
    }

    @Override
    public Similarity visit(CharValue charValue1) {
        CharValue charValue2 = (CharValue) val2;
        boolean sameValue = charValue1.getCharContent() == charValue2.getCharContent();
        return new ExpressionSimilarity<>(sameValue ? 1 : 0, 1, charValue1, charValue2);
    }

    @Override
    public Similarity visit(BooleanValue booleanValue1) {
        BooleanValue booleanValue2 = (BooleanValue) val2;
        boolean sameValue = booleanValue1.isTrue() == booleanValue2.isTrue();
        return new ExpressionSimilarity<>(sameValue ? 1 : 0, 1, booleanValue1, booleanValue2);
    }

}
