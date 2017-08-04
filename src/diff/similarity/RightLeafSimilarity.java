package diff.similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import diff.complexity.Showable;

/**
 * Comparison analysis between nothing on the left, and a right side code.
 *
 * @param <T> the type of object analyzed on the right
 */
public class RightLeafSimilarity<T extends Showable> extends Similarity {

    protected final List<T> obj2;

    /**
     * Build a similarity analysis between nothing on the left and an optional right Object.
     * @param <T> the type of object analyzed
     * @param amount the size
     * @param optionalObj2 the optional right object
     * @return a {@link Similarity}
     */
    public static <T extends Showable> Similarity build(int amount, Optional<T> optionalObj2) {
        if (optionalObj2.isPresent()) {
            return new RightLeafSimilarity<>(amount, optionalObj2.get());
        } else {
            return new NoSimilarity();
        }
    }

    /**
     * Build a similarity analysis between nothing on the left and a series of Objects on the right.<br>
     * If the list is empty, then a {@link NoSimilarity} object is returned.
     * @param <T> the type of object analyzed
     * @param amount the size
     * @param obj2 the left objects
     * @return a {@link Similarity}
     */
    public static <T extends Showable> Similarity build(int amount, List<T> obj2) {
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
    public RightLeafSimilarity(int amount, T obj2) {
        super(0, amount);
        this.obj2 = Arrays.asList(obj2);
    }

    /**
     * Build a similarity analysis between nothing on the left and a series of Objects on the right.
     * @param amount the size
     * @param obj2 the left objects
     */
    private RightLeafSimilarity(int amount, List<T> obj2) {
        super(0, amount);
        this.obj2 = obj2;
    }

    @Override
    public List<String[]> show(String prefix) {
        List<String[]> result = new ArrayList<>();
        for (T obj : obj2) {
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
}
