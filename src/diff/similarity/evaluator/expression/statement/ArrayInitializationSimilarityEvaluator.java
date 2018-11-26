package diff.similarity.evaluator.expression.statement;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.expression.statement.ArrayInitializationSizer;
import diff.similarity.LeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitialisationLeaf;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitialisationRec;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitialization;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitializationVisitor;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArraySizeDeclaration;

/**
 * Compares two {@link ArrayInitialization}s.
 */
public class ArrayInitializationSimilarityEvaluator extends SimilarityEvaluator<ArrayInitialization>
	implements ArrayInitializationVisitor<DualArrayInitializationComparator<? extends ArrayInitialization>> {

	/** Instance */
	public static final ArrayInitializationSimilarityEvaluator INSTANCE = new ArrayInitializationSimilarityEvaluator();

	private ArrayInitializationSimilarityEvaluator() {
		super(ArrayInitializationSizer.ARRAY_INITIALIZATION_SIZER, "arrayDecl");
	}

	@Override
	public Similarity eval(ArrayInitialization array1, ArrayInitialization array2) {
		if (array1.getClass().equals(array2.getClass())) {
			DualArrayInitializationComparator<? extends ArrayInitialization> dualComparator = array2.accept(this);
			return array1.accept(dualComparator);
		} else {
			return new LeafSimilarity<ArrayInitialization>("ArrayInit", 0., ArrayInitializationSizer.ARRAY_INITIALIZATION_SIZER.size(array1, array2), array1,
				array2) {

				@Override
				public List<String[]> show(String prefix) {
					List<String[]> list = new ArrayList<>();
					list.add(new String[] { prefix + array1, "0", prefix + array2 });
					return list;
				}
			};
		}
	}

	@Override
	public DualArrayInitializationComparator<ArrayInitialisationLeaf> visit(ArrayInitialisationLeaf leaf) {
		return new DualArrayInitializationComparator<ArrayInitialisationLeaf>(leaf);
	}

	@Override
	public DualArrayInitializationComparator<ArrayInitialisationRec> visit(ArrayInitialisationRec rec) {
		return new DualArrayInitializationComparator<ArrayInitialisationRec>(rec);
	}

	@Override
	public DualArrayInitializationComparator<ArraySizeDeclaration> visit(ArraySizeDeclaration decla) {
		return new DualArrayInitializationComparator<ArraySizeDeclaration>(decla);
	}
}
