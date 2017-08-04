package diff.similarity.evaluator.expression.statement;

import java.util.ArrayList;
import java.util.List;

import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.DualExpressionComparator;
import lombok.AllArgsConstructor;
import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.statement.ArrayDeclaration.ArrayInitialisationLeaf;
import parser.syntaxtree.expression.statement.ArrayDeclaration.ArrayInitialisationRec;
import parser.syntaxtree.expression.statement.ArrayDeclaration.ArrayInitialization;
import parser.syntaxtree.expression.statement.ArrayDeclaration.ArrayInitializationVisitor;
import parser.syntaxtree.expression.statement.ArrayDeclaration.ArraySizeDeclaration;

/**
 * Compares two {@link ArrayInitialization}s that must have the same type.
 *
 * @param <T> the common type of array initialization
 */

@AllArgsConstructor
public class DualArrayInitializationComparator<T extends ArrayInitialization> implements ArrayInitializationVisitor<Similarity> {

    private final T array2;

    @Override
    public Similarity visit(ArrayInitialisationLeaf leaf1) {
        ArrayInitialisationLeaf leaf2 = (ArrayInitialisationLeaf) array2;
        Similarity listDiff = StatementSimilarityEvaluator.INSTANCE.orderedEval(leaf1.getValues(), leaf2.getValues());
        return listDiff;
    }

    @Override
    public Similarity visit(ArrayInitialisationRec rec1) {
        ArrayInitialisationRec rec2 = (ArrayInitialisationRec) array2;
        Similarity listDiff = ArrayInitializationSimilarityEvaluator.INSTANCE.orderedEval(rec1.getSubDimensions(), rec2.getSubDimensions());
        return listDiff;
    }

    @Override
    public Similarity visit(ArraySizeDeclaration decla1) {
        ArraySizeDeclaration decla2 = (ArraySizeDeclaration) array2;
        Expression[] dim1 = decla1.getDimensions();
        Expression[] dim2 = decla2.getDimensions();
        List<Similarity> similarities = new ArrayList<>();
        for (int num = 0; num < Math.min(dim1.length, dim2.length); num++) {
            Similarity similarity = dim1[num].acceptExpressionVisitor(new DualExpressionComparator<>(dim2[num]));
            similarities.add(similarity);
        }
        return Similarity.add("ArraySizeDecla", similarities);
    }
}
