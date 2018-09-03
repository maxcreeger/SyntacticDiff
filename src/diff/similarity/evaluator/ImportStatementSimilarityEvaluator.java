package diff.similarity.evaluator;

import lexeme.java.tree.ImportStatement;
import diff.complexity.ImportStatementSizer;
import diff.similarity.Similarity;

/**
 * Compares two {@link ImportStatement}s.
 */
public class ImportStatementSimilarityEvaluator extends SimilarityEvaluator<ImportStatement> {

	/** Instance. */
	public static final ImportStatementSimilarityEvaluator INSTANCE = new ImportStatementSimilarityEvaluator();

	private ImportStatementSimilarityEvaluator() {
		super(ImportStatementSizer.IMPORT_STATEMENT_SIZER, "import");
	}

	@Override
	public Similarity eval(ImportStatement import1, ImportStatement import2) {
		return Similarity.eval(import1.getImportStatement(), import2.getImportStatement());
	}
}
