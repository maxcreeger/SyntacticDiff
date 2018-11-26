package diff.similarity.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import diff.complexity.ClassNameSizer;
import diff.complexity.Showable;
import diff.similarity.CompositeSimilarity;
import diff.similarity.LeafSimilarity;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.NoSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import lexeme.java.tree.ClassName;
import lexeme.java.tree.ClassName.ArrayDimension;
import prettyprinting.SimilarityChainingHint;

/**
 * Compares two {@link ClassName}s.
 */
public class ClassNameSimilarityEvaluator extends SimilarityEvaluator<ClassName> {

	/** Instance. */
	public static final ClassNameSimilarityEvaluator INSTANCE = new ClassNameSimilarityEvaluator();

	public static class ClassNameSimilarity extends CompositeSimilarity {

		public static ClassNameSimilarity build(Similarity dimSimilarity, Similarity superSimilarity, Similarity nameSimilarity,
			List<Similarity> genericSimilarity) {
			List<Similarity> allSims = new ArrayList<>();
			allSims.add(dimSimilarity);
			allSims.add(superSimilarity);
			allSims.add(nameSimilarity);
			allSims.addAll(genericSimilarity);
			double same = 0;
			int amount = 0;
			for (Similarity similarity : allSims) {
				if (similarity.isEmpty()) {
					continue; // ignore
				}
				same += similarity.getSame();
				amount += similarity.getAmount();
			}
			return new ClassNameSimilarity(same, amount, dimSimilarity, superSimilarity, nameSimilarity, genericSimilarity);
		}

		protected ClassNameSimilarity(double same, int amount, Similarity dimSimilarity, Similarity superSimilarity, Similarity nameSimilarity,
			List<Similarity> genericSimilarity) {
			super("classname", same, amount);
			super.addOne(dimSimilarity, SimilarityChainingHint.startWith("[").inLineWithSeparator(", ").endWith("]"));
			super.addOne(superSimilarity, SimilarityChainingHint.startWith(" super ").inLine().end());
			super.addOne(nameSimilarity, SimilarityChainingHint.startWith(" ").inLine().end());
			super.addAll(genericSimilarity, SimilarityChainingHint.startWith("<").inLineWithSeparator(", ").endWith(">"));
		}

		@Override
		public <T> T accept(SimilarityVisitor<T> visitor) {
			return visitor.visit(this);
		}

	}

	private ClassNameSimilarityEvaluator() {
		super(ClassNameSizer.CLASS_NAME_SIZER, "classname");
	}

	@Override
	public Similarity eval(ClassName class1, ClassName class2) {
		// Dimensions
		final Similarity dimSimilarity;
		if (!class1.getArrayDimension().isPresent()) {
			if (!class2.getArrayDimension().isPresent()) {
				dimSimilarity = new NoSimilarity(); // Nothing to compare
			} else {
				dimSimilarity = new RightLeafSimilarity<Showable>(1, class2.getArrayDimension().get());
			}
		} else {
			if (!class2.getArrayDimension().isPresent()) {
				dimSimilarity = new LeftLeafSimilarity<Showable>(1, class1.getArrayDimension().get());
			} else {
				dimSimilarity = new LeafSimilarity<ArrayDimension>("arrayDim", 0, 1, class1.getArrayDimension().get(), class2.getArrayDimension().get()) {

					@Override
					public List<String[]> show(String prefix) {
						List<String[]> list = new ArrayList<>();
						list.add(new String[] { prefix + class1, "0", prefix + class2 });
						return list;
					}
				};
			}
		}

		// 'Super' keyword
		final Similarity superSimilarity;
		if (class1.getSuperClass().isPresent()) {
			if (class2.getSuperClass().isPresent()) {
				superSimilarity = this.eval(class1.getSuperClass().get(), class2.getSuperClass().get());
			} else {
				superSimilarity = new LeftLeafSimilarity<>(sizer.size(class1.getSuperClass().get()), class1.getSuperClass().get());
			}
		} else {
			if (class2.getSuperClass().isPresent()) {
				superSimilarity = new RightLeafSimilarity<>(sizer.size(class2.getSuperClass().get()), class2.getSuperClass().get());
			} else {
				superSimilarity = new NoSimilarity();
			}
		}
		// Naming
		Similarity nameSimilarity = Similarity.eval(class1.getName(), class2.getName());
		// Sub parameters (Generic type arguments)
		List<Similarity> genericSimilarity;
		if (class1.getNestedSubParameters() == null) {
			if (class2.getNestedSubParameters() == null || class2.getNestedSubParameters().isEmpty()) { // TODO differentiate null vs empty
				genericSimilarity = new ArrayList<>();
			} else {
				genericSimilarity = class2	.getNestedSubParameters().stream()
											.map(param -> RightLeafSimilarity.build(ClassNameSizer.CLASS_NAME_SIZER.size(param), param))
											.collect(Collectors.toList());
			}
		} else {
			if (class1.getNestedSubParameters().isEmpty() || class2.getNestedSubParameters() == null) { // TODO differentiate null vs empty
				genericSimilarity = class1	.getNestedSubParameters().stream()
											.map(param -> LeftLeafSimilarity.build(ClassNameSizer.CLASS_NAME_SIZER.size(param), param))
											.collect(Collectors.toList());
			} else {
				genericSimilarity = this.compareWithGapsList(class1.getNestedSubParameters(), class2.getNestedSubParameters());
			}
		}
		// TODO use hints
		// Compile all (LOL compile!)
		return ClassNameSimilarity.build(dimSimilarity, superSimilarity, nameSimilarity, genericSimilarity);
	}
}
