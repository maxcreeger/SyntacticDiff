package diff.similarity.evaluator;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.RootSizer;
import diff.similarity.CompositeSimilarity;
import diff.similarity.Similarity;
import lexeme.java.tree.Root;
import prettyprinting.SimilarityChainingHint;

/**
 * Compares two {@link Root}s.
 */

public class RootSimilarityEvaluator extends SimilarityEvaluator<Root> {

	public static class RootSimilarity extends CompositeSimilarity {

		protected RootSimilarity(String name, double same, int amount, Similarity packageSimilarity, List<Similarity> importSimilarity,
			Similarity classSimilarity) {
			super(name, same, amount);
			super.addOne(packageSimilarity, SimilarityChainingHint.start().inLine().end());
			super.addAll(importSimilarity, SimilarityChainingHint.start().newLinesWithPrefix("import").end());
			super.addOne(classSimilarity, SimilarityChainingHint.start().newLines().end());

			if (super.contents.isEmpty()) {
				throw new UnsupportedOperationException("No content inside an aggregate???");
			}
		}

		public static RootSimilarity build(Similarity packageSimilarity, List<Similarity> importSimilarity, Similarity classSimilarity) {
			List<Similarity> allSims = new ArrayList<>();
			allSims.add(packageSimilarity);
			allSims.addAll(importSimilarity);
			allSims.add(classSimilarity);

			double same = 0;
			int amount = 0;
			for (Similarity similarity : allSims) {
				if (similarity.isEmpty()) {
					continue; // ignore
				}
				same += similarity.getSame();
				amount += similarity.getAmount();
			}
			return new RootSimilarity("root", same, amount, packageSimilarity, importSimilarity, classSimilarity);
		}

		@Override
		public <T> T accept(SimilarityVisitor<T> visitor) {
			return visitor.visit(this);
		}
	}

	/** Instance. */
	public static final RootSimilarityEvaluator INSTANCE = new RootSimilarityEvaluator();

	private RootSimilarityEvaluator() {
		super(RootSizer.ROOT_SIZER, "root");
	}

	@Override
	public Similarity eval(Root root1, Root root2) {
		Similarity packageSimilarity = PackageDeclarationSimilarityEvaluator.INSTANCE.eval(root1.getPackageDeclaration(), root2.getPackageDeclaration());
		List<Similarity> importSimilarity = ImportStatementSimilarityEvaluator.INSTANCE.maximumMatchList(root1.getImports(), root2.getImports());
		Similarity classSimilarity = ClassDeclarationSimilarityEvaluator.INSTANCE.eval(root1.getClassDeclaration(), root2.getClassDeclaration());
		return RootSimilarity.build(packageSimilarity, importSimilarity, classSimilarity);
	}
}
