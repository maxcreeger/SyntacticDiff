package diff.similarity.evaluator;

import lexeme.java.tree.PackageDeclaration;
import diff.complexity.PackageDeclarationSizer;
import diff.similarity.Similarity;

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
		return Similarity.eval(pack1.getPackageDeclaration(), pack2.getPackageDeclaration());
	}
}
