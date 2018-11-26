package diff.similarity;

import diff.similarity.RightLeafSimilarity.NoShowable;
import tokenizer.CodeLocator;

/**
 * No similarity at all (no two objects matched).
 */
public class NoSimilarity extends SimpleSimilarity {

	private static final ShowableString nothing = new ShowableString("<nothing>", new CodeLocator("").branch().commit());

	/**
	 * Builds a {@link NoSimilarity}.
	 */
	public NoSimilarity() {
		super(0, 0, nothing, "?", nothing);
	}

	@Override
	public double similarity() {
		return 0; // Avoids division by zero
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public NoShowable showLeft() {
		return new NoShowable();
	}

	@Override
	public NoShowable showRight() {
		return new NoShowable();
	}

	@Override
	public <T> T accept(SimilarityVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
