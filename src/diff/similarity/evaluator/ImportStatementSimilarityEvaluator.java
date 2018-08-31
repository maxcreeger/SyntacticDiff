package diff.similarity.evaluator;

import diff.complexity.ImportStatementSizer;
import diff.similarity.Similarity;
import lexeme.java.tree.ImportStatement;

/**
 * Compares two {@link ImportStatement}s.
 */
public class ImportStatementSimilarityEvaluator extends SimilarityEvaluator<ImportStatement> {

    /** Instance. */
    public static final ImportStatementSimilarityEvaluator INSTANCE = new ImportStatementSimilarityEvaluator();

    private ImportStatementSimilarityEvaluator() {
        super(ImportStatementSizer.IMPORT_STATEMENT_SIZER, "import");
    }

    public Similarity eval(ImportStatement import1, ImportStatement import2) {
        return Similarity.eval(import1.toString(), import2.toString());
    }
}
