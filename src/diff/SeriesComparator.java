package diff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import diff.complexity.Showable;
import diff.complexity.SyntaxSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.NoSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Compares two series of items, order does matter.
 *
 * @param <T> the type of items in the series
 */
@Getter
@AllArgsConstructor
public class SeriesComparator<T extends Showable> {

    private final SimilarityEvaluator<T> evaluator;
    private final SyntaxSizer<T> sizer;

    /**
     * Compares the two input list members, one by one in order and produces a Similarity analysis of them.<br>
     * Inserts gaps in some sides to make a better matching.
     * @param name the name of the global function of this bundle
     * @param listA left side
     * @param listB right side
     * @return a similarity analysis
     */
    public Similarity compareOrderly(String name, List<T> listA, List<T> listB) {
        List<Similarity> result = compareOrderlyWithGaps(listA, listB);
        return Similarity.add(name, result);
    }

    /**
     * Compares the two input list members, one by one in order and produces a Similarity analysis of them.<br>
     * Statements are compared in order without jumps or gaps
     * @param listA left side
     * @param listB right side
     * @return a similarity analysis
     */
    public List<Similarity> compareOrderlyNoChange(List<T> listA, List<T> listB) {
        List<Similarity> globalComp = new ArrayList<>();
        for (int i = 0; i < Math.max(listA.size(), listB.size()); i++) {
            T elemA = null;
            T elemB = null;
            if (i < listA.size()) {
                elemA = listA.get(i);
            }
            if (i < listB.size()) {
                elemB = listB.get(i);
            }
            Similarity compAB = compareElements(elemA, elemB);
            globalComp.add(compAB);
        }

        return globalComp;
    }

    private List<Similarity> compareOrderlyWithGaps(List<T> listA, List<T> listB) {
        if (listA.isEmpty()) {
            List<Similarity> result = new ArrayList<>();
            for (T elemB : listB) {
                result.add(new RightLeafSimilarity<T>(sizer.size(elemB), elemB));
            }
            return result;
        } else if (listB.isEmpty()) {
            List<Similarity> result = new ArrayList<>();
            for (T elemA : listA) {
                result.add(new LeftLeafSimilarity<T>(sizer.size(elemA), elemA));
            }
            return result;
        } else { // None empty
            List<Similarity> bestResult = null;
            { // Attempt to insert some left gaps
                for (int nbGapsA = 0; nbGapsA < listB.size(); nbGapsA++) { // maybe no gap (straight match?)
                    List<T> listBcopy = new ArrayList<>(listB);
                    List<T> listAcopy = new ArrayList<>(listA);
                    // Make the gaps
                    List<Similarity> thisResult = new ArrayList<>();
                    for (int i = 0; i < nbGapsA; i++) {
                        final T itemB = listBcopy.remove(0);
                        final RightLeafSimilarity<T> gap = new RightLeafSimilarity<>(sizer.size(itemB), itemB);
                        thisResult.add(gap);
                    }
                    // Make 1 match
                    T elemA = listAcopy.remove(0);
                    T elemB = listBcopy.remove(0);
                    Similarity simAB = compareElements(elemA, elemB);
                    List<Similarity> rest = compareOrderlyWithGaps(listAcopy, listBcopy);
                    thisResult.add(simAB);
                    thisResult.addAll(rest);
                    if (bestResult == null || totalSimilarity(bestResult) < totalSimilarity(thisResult)) {
                        bestResult = thisResult;
                    }
                }
            }
            { // Attempt to insert some left gaps
                for (int nbGapsB = 1; nbGapsB < listA.size(); nbGapsB++) { // At least 1 gap
                    List<T> listAcopy = new ArrayList<>(listA);
                    List<T> listBcopy = new ArrayList<>(listB);
                    // Make the gaps
                    List<Similarity> thisResult = new ArrayList<>();
                    for (int i = 0; i < nbGapsB; i++) {
                        final T itemA = listAcopy.remove(0);
                        final LeftLeafSimilarity<T> gap = new LeftLeafSimilarity<>(sizer.size(itemA), itemA);
                        thisResult.add(gap);
                    }
                    // Make 1 match
                    T elemA = listAcopy.remove(0);
                    T elemB = listBcopy.remove(0);
                    Similarity simAB = compareElements(elemA, elemB);
                    List<Similarity> rest = compareOrderlyWithGaps(listAcopy, listBcopy);
                    thisResult.add(simAB);
                    thisResult.addAll(rest);
                    if (bestResult == null || totalSimilarity(bestResult) < totalSimilarity(thisResult)) {
                        bestResult = thisResult;
                    }
                }
            }
            return bestResult;
        }
    }

    private double totalSimilarity(List<Similarity> input) {
        return input.stream().collect(Collectors.summingDouble(Similarity::similarity));
    }

    private Similarity compareElements(T elemA, T elemB) {
        if (elemA == null) {
            if (elemB == null) {
                return new NoSimilarity();
            } else {
                return new RightLeafSimilarity<T>(sizer.size(elemB), elemB);
            }
        } else {
            if (elemB == null) {
                return new LeftLeafSimilarity<T>(sizer.size(elemA), elemA);
            } else {
                return evaluator.eval(elemA, elemB);
            }
        }
    }
}
