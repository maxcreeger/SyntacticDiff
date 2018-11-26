package diff.similarity.evaluator;

import diff.complexity.ImportStatementSizer;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity.ShowableString;
import diff.similarity.StringSimilarity;
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

	@Override
	public Similarity eval(ImportStatement import1, ImportStatement import2) {
		ShowableString leftString = new ShowableString(import1.getImportStatement(), import1.getLocation());
		ShowableString rightString = new ShowableString(import2.getImportStatement(), import2.getLocation());
		return StringSimilarity.eval(leftString, rightString);
	}
}
