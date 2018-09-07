package diff.similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import diff.complexity.Showable;
import diff.similarity.CompositeSimilarity.AggregateShowable;
import lombok.Getter;

/**
 * Comparison analysis between nothing on the left, and a right side code.
 *
 * @param <S> the type of object analyzed on the right
 */
@Getter
public class RightLeafSimilarity<S extends Showable> extends Similarity {

    public static final class NoShowable implements Showable {
        @Override
        public List<String> show(String prefix) {
            return new ArrayList<>();
        }
    }

    protected final List<S> obj2;

    /**
     * Build a similarity analysis between nothing on the left and an optional right Object.
     * @param <S> the type of object analyzed
     * @param amount the size
     * @param optionalObj2 the optional right object
     * @return a {@link Similarity}
     */
    public static <S extends Showable> Similarity build(int amount, Optional<S> optionalObj2) {
        if (optionalObj2.isPresent()) {
            return new RightLeafSimilarity<>(amount, optionalObj2.get());
        } else {
            return new NoSimilarity();
        }
    }

    /**
     * Build a similarity analysis between nothing on the left and a series of Objects on the right.<br>
     * If the list is empty, then a {@link NoSimilarity} object is returned.
     * @param <S> the type of object analyzed
     * @param amount the size
     * @param obj2 the left objects
     * @return a {@link Similarity}
     */
    public static <S extends Showable> Similarity build(int amount, List<S> obj2) {
        if (obj2.isEmpty()) {
            return new NoSimilarity();
        } else {
            return new RightLeafSimilarity<>(amount, obj2);
        }
    }

    /**
     * Build a similarity analysis between nothing on the left and a right Object.
     * @param amount the size
     * @param obj2 the right object
     */
    public RightLeafSimilarity(int amount, S... obj2) {
        super(0, amount);
        this.obj2 = Arrays.asList(obj2);
    }

    /**
     * Build a similarity analysis between nothing on the left and a series of Objects on the right.
     * @param amount the size
     * @param obj2 the left objects
     */
    private RightLeafSimilarity(int amount, List<S> obj2) {
        super(0, amount);
        this.obj2 = obj2;
    }

    @Override
    public List<String[]> show(String prefix) {
        List<String[]> result = new ArrayList<>();
        for (S obj : obj2) {
            for (String line : obj.show(prefix)) {
                result.add(new String[] {prefix + "<Nothing>", "RIGHT", line});
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "<Right only=" + obj2 + ">";
    }

    @Override
    public <R> R accept(SimilarityVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Showable showLeft() {
        return new NoShowable();
    }

    @Override
    public Showable showRight() {
        return new AggregateShowable((List<Showable>) obj2);
    }

    @Override
    public List<Similarity> subSimilarities() {
        // TODO Auto-generated method stub
        return null;
    }
}
