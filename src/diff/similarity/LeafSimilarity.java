package diff.similarity;

/**
 * Similarity analysis between two objects of the same type.
 *
 * @param <T> the type of the objects compared
 */
public abstract class LeafSimilarity<T> extends Similarity {

    protected final T obj1;
    protected final T obj2;

    /**
     * Builds a comparison between two objects, using the known similarity metric and size metric.
     * @param same the similarity
     * @param amount the size of the object
     * @param obj1 the left object
     * @param obj2 the right object
     */
    public LeafSimilarity(double same, int amount, T obj1, T obj2) {
        super(same, amount);
        this.obj1 = obj1;
        this.obj2 = obj2;
    }
}
