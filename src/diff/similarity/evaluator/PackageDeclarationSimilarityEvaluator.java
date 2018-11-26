package diff.similarity.evaluator;

import diff.complexity.PackageDeclarationSizer;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity.ShowableString;
import lexeme.java.tree.PackageDeclaration;

/**
 * Compares two {@link PackageDeclaration}s.
 */
public class PackageDeclarationSimilarityEvaluator extends SimilarityEvaluator<PackageDeclaration> {

	/** Instance. */
	public static final PackageDeclarationSimilarityEvaluator INSTANCE = new PackageDeclarationSimilarityEvaluator();

	private PackageDeclarationSimilarityEvaluator() {
		super(PackageDeclarationSizer.PACKAGE_DECLARATION_SIZER, "package");
	}

	@Override
	public Similarity eval(PackageDeclaration pack1, PackageDeclaration pack2) {
		ShowableString left = new ShowableString(pack1.getPackageDeclaration(), pack1.getLocation());
		ShowableString right = new ShowableString(pack2.getPackageDeclaration(), pack2.getLocation());
		return Similarity.eval(left, right);
	}
}
