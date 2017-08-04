package diff.similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import diff.complexity.Showable;

/**
 * Comparison analysis between a left side code, and nothing on the right.
 *
 * @param <T> the type of object analyzed on the left
 */
public class LeftLeafSimilarity<T extends Showable> extends Similarity {

    protected final List<T> obj1;

    /**
     * Build a similarity analysis between a series of left Objects and nothing on the right. If the list is empty, then a {@link NoSimilarity} object is returned.
     * @param <T> the type of object analyzed
     * @param amount the size
     * @param obj2 the left objects
     * @return a {@link Similarity}
     */
    public static <T extends Showable> Similarity build(int amount, List<T> obj2) {
        if (obj2.isEmpty()) {
            return new NoSimilarity();
        } else {
            return new LeftLeafSimilarity<>(amount, obj2);
        }
    }

    /**
     * Build a similarity analysis between a left Object and nothing on the right.
     * @param amount the size
     * @param obj1 the left object
     */
    public LeftLeafSimilarity(int amount, T obj1) {
        super(0, amount);
        this.obj1 = Arrays.asList(obj1);
    }

    /**
     * Build a similarity analysis between a series of left Objects and nothing on the right.
     * @param amount the size
     * @param obj1 the left objects
     */
    private LeftLeafSimilarity(int amount, List<T> obj1) {
        super(0, amount);
        this.obj1 = obj1;
    }

    @Override
    public List<String[]> show(String prefix) {
        List<String[]> objShow = new ArrayList<>();
        for (T obj : obj1) {
            for (String line : obj.show(prefix)) {
                objShow.add(new String[] {line, "LEFT", prefix + "<Nothing>"});
            }
        }
        return objShow;
    }

    @Override
    public String toString() {
        return "<Left only:" + obj1 + ">";
    }
}
