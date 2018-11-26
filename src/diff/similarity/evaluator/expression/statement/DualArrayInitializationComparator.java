package diff.similarity.evaluator.expression.statement;

import java.util.ArrayList;
import java.util.List;

import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.DualExpressionComparator;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitialisationLeaf;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitialisationRec;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitialization;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitializationVisitor;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArraySizeDeclaration;
import lombok.AllArgsConstructor;

/**
 * Compares two {@link ArrayInitialization}s that must have the same type.
 *
 * @param <T>
 *            the common type of array initialization
 */

@AllArgsConstructor
public class DualArrayInitializationComparator<T extends ArrayInitialization> implements ArrayInitializationVisitor<Similarity> {

	private final T array2;

	@Override
	public Similarity visit(ArrayInitialisationLeaf leaf1) {
		ArrayInitialisationLeaf leaf2 = (ArrayInitialisationLeaf) array2;
		Similarity listDiff = StatementSimilarityEvaluator.INSTANCE.compareWithGaps(leaf1.getValues(), leaf2.getValues());
		return listDiff;
	}

	@Override
	public Similarity visit(ArrayInitialisationRec rec1) {
		ArrayInitialisationRec rec2 = (ArrayInitialisationRec) array2;
		Similarity listDiff = ArrayInitializationSimilarityEvaluator.INSTANCE.strictOrder(rec1.getSubDimensions(), rec2.getSubDimensions());
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
