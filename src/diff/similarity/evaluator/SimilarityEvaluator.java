package diff.similarity.evaluator;

import java.util.List;
import java.util.Optional;

import diff.MaximumMatching;
import diff.SeriesComparator;
import diff.complexity.Showable;
import diff.complexity.SyntaxSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.NoSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import lombok.AllArgsConstructor;

/**
 * Tool to evaluate similarity between some types of objects.
 *
 * @param <T> the type of objects whose similarity can be evaluated
 */
@AllArgsConstructor
public abstract class SimilarityEvaluator<T extends Showable> {

    protected final SyntaxSizer<T> sizer;
    protected final String name;

    /**
     * Evaluates similarity between two objects
     * @param obj1 the left side
     * @param obj2 the right side
     * @return a {@link Similarity}
     */
    public abstract Similarity eval(T obj1, T obj2);

    /**
     * Evaluates the similarity between two lists, while respecting the lists' order.<br>
     * However gaps can be inserted to allow better matching such as:<br>
     * A ------ A<br>
     * (gap) -- B<br>
     * C ------ C
     * @param listA the left side
     * @param listB the right side
     * @return a {@link Similarity}
     */
    public Similarity orderedEval(List<T> listA, List<T> listB) {
        return new SeriesComparator<>(this, sizer).compareOrderly(name + " / list", listA, listB);
    }

    /**
     * Evaluates the similarity between two lists, performing maximum matching of elements regardless of the lists' order.
     * @param listA the left side
     * @param listB the right side
     * @return a {@link Similarity}
     */
    public Similarity maximumMatch(List<T> listA, List<T> listB) {
        return new MaximumMatching<>(this, sizer).compare(name + " / set", listA, listB);
    }

    /**
     * Evaluates similarity between optional objects.
     * @param optA left side
     * @param optB right side
     * @return a similarity evaluation
     */
    public Similarity eval(Optional<? extends T> optA, Optional<? extends T> optB) {
        if (optA.isPresent()) {
            if (optB.isPresent()) {
                return this.eval(optA.get(), optB.get());
            } else {
                return new LeftLeafSimilarity<>(sizer.size(optA.get()), optA.get());
            }
        } else {
            if (optB.isPresent()) {
                return new RightLeafSimilarity<>(sizer.size(optB.get()), optB.get());
            } else {
                return new NoSimilarity();
            }
        }
    }

}
