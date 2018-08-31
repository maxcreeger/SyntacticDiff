package diff.similarity.evaluator;

import diff.complexity.RootSizer;
import diff.similarity.Similarity;
import lexeme.java.tree.Root;

/**
 * Compares two {@link Root}s.
 */

public class RootSimilarityEvaluator extends SimilarityEvaluator<Root> {

    /** Instance. */
    public static final RootSimilarityEvaluator INSTANCE = new RootSimilarityEvaluator();

    private RootSimilarityEvaluator() {
        super(RootSizer.ROOT_SIZER, "root");
    }

    public Similarity eval(Root root1, Root root2) {
        Similarity packageSimilarity =
                PackageDeclarationSimilarityEvaluator.INSTANCE.eval(root1.getPackageDeclaration(), root2.getPackageDeclaration());
        Similarity importSimilarity = ImportStatementSimilarityEvaluator.INSTANCE.maximumMatch(root1.getImports(), root2.getImports());
        Similarity classSimilarity = ClassDeclarationSimilarityEvaluator.INSTANCE.eval(root1.getClassDeclaration(), root2.getClassDeclaration());
        return Similarity.add(name, packageSimilarity, importSimilarity, classSimilarity);
    }
}

