package diff.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import diff.Levenshtein;
import diff.complexity.Showable;
import diff.complexity.SyntaxSizer;
import diff.similarity.evaluator.SimilarityEvaluator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the similarity analysis between two code trees.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Similarity {

    private final double same;
    private final int amount;

    /**
     * Return the similarity rate.
     * @return the similarity rate (1.0 is exact similarity)
     */
    public double similarity() {
        return same / amount;
    }

    /**
     * Holds no information (irrelevant and should be ignored).
     * @return a boolean
     */
    boolean isEmpty() {
        return false;
    }

    @Override
    public String toString() {
        final int padding = 60;
        StringBuilder builder = new StringBuilder();
        final List<String[]> show = show("");
        for (String[] strings : show) {
            String formatted = String.format("%0$-" + padding + "." + padding + "s (%0$-5.5s)  %s", strings[0], strings[1], strings[2]);
            builder.append(formatted + "\n");
        }
        return builder.toString();
    }

    /**
     * Bundles several {@link Similarity} analysis together.
     * @param name the name of the aggregate
     * @param sims the smaller similarities
     * @return a composite similarity
     */
    public static Similarity add(String name, List<Similarity> sims) {
        return add(name, sims.toArray(new Similarity[sims.size()]));
    }

    /**
     * Bundles several {@link Similarity} analysis together.
     * 
     * @param name the name of the aggregate
     * @param sims the smaller similarities
     * @return a composite similarity
     */
    public static Similarity add(String name, Similarity... sims) {
        double same = 0;
        int amount = 0;
        List<Similarity> actual = new ArrayList<>();
        for (Similarity similarity : sims) {
            if (similarity.isEmpty()) {
                continue; // ignore
            }
            same += similarity.same;
            amount += similarity.amount;
            actual.add(similarity);
        }
        if (actual.size() == 0) {
            return new NoSimilarity();
        } else if (actual.size() == 1) {
            return actual.get(0);
        } else {
            return new CompositeSimilarity(name, same, amount, actual.toArray(new Similarity[actual.size()]));
        }
    }

    /**
     * Show the similarity between the both code snippet side by side.
     * @param prefix the prefix on the left
     * @return a {@link List} of lines, each made of two Strings in an Array
     */
    public abstract List<String[]> show(String prefix);

    /**
     * Compares two string using Levenshtein distance.
     * @param strA string 1
     * @param strB string 2
     * @return distance : 0 (completely different) ... 1 (all characters matching))
     */
    public static LeafSimilarity<String> eval(String strA, String strB) {
        int dist = new Levenshtein(strA, strB).computeDistance();
        int maxLen = Math.max(strA.length(), strB.length());
        return new StringSimilarity((maxLen - dist) / (double) maxLen, 1, strA, strB);
    }

    /**
     * Evaluates the similarity between some optional objects using an evaluator and a sizer
     * @param <T> the type of objects evaluated
     * @param optA the left object (optional)
     * @param optB the right object (optional)
     * @param simEval a similarity evaluator for an object of type T
     * @param sizer a sizer for an object of type T
     * @return the similarity analysis
     */
    public static <T extends Showable> Similarity eval(Optional<? extends T> optA, Optional<? extends T> optB, SimilarityEvaluator<T> simEval,
            SyntaxSizer<T> sizer) {
        if (optA.isPresent()) {
            if (optB.isPresent()) {
                return simEval.eval(optA.get(), optB.get());
            } else {
                return new LeftLeafSimilarity<T>(sizer.size(optA.get()), optA.get());
            }
        } else {
            if (optB.isPresent()) {
                return new RightLeafSimilarity<T>(sizer.size(optB.get()), optB.get());
            } else {
                return new NoSimilarity();
            }
        }
    }
}