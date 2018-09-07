package diff.similarity;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.Showable;
import lombok.Getter;

/**
 * Similarity analysis between two objects of the same type.
 *
 * @param <S> the type of the objects compared
 */
@Getter
public abstract class LeafSimilarity<S extends Showable> extends Similarity {

    protected final S obj1;
    protected final S obj2;

    /**
     * Builds a comparison between two objects, using the known similarity metric and size metric.
     * @param same the similarity
     * @param amount the size of the object
     * @param obj1 the left object
     * @param obj2 the right object
     */
    public LeafSimilarity(double same, int amount, S obj1, S obj2) {
        super(same, amount);
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    @Override
    public Showable showLeft() {
        return obj1;
    }

    @Override
    public Showable showRight() {
        return obj2;
    }

    @Override
    public List<Similarity> subSimilarities() {
        return new ArrayList<>();
    }

    @Override
    public <T> T accept(SimilarityVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
