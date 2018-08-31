package diff.similarity.evaluator;

import diff.complexity.PackageDeclarationSizer;
import diff.similarity.Similarity;
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

    public Similarity eval(PackageDeclaration pack1, PackageDeclaration pack2) {
        return Similarity.eval(pack1.toString(), pack2.toString());
    }
}
