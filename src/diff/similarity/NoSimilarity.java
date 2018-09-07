package diff.similarity;

/**
 * No similarity at all (no two objects matched).
 */
public class NoSimilarity extends SimpleSimilarity {

    /**
     * Builds a {@link NoSimilarity}.
     */
    public NoSimilarity() {
        super(0, 0, "<nothing>", "?", "<nothing>");
    }

    @Override
    public double similarity() {
        return 0; // Avoids division by zero
    }

    boolean isEmpty() {
        return true;
    }

    @Override
    public <T> T accept(SimilarityVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
