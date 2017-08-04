package diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import diff.complexity.Showable;
import diff.complexity.SyntaxSizer;
import diff.complexity.UnitySizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Makes a comparison of two sets of items, matching them as best it can.
 *
 * @param <T> the expected type of the objects, which must be {@link Showable}
 */
@AllArgsConstructor
public class MaximumMatching<T extends Showable> {

    private final SimilarityEvaluator<T> similarityEvaluator;
    private final SyntaxSizer<T> sizer;

    /**
     * Creates a comparison of two sets of items, matching the best it can (no order is respected)
     * @param name the name of the global function of this bundle
     * @param list1 the left side
     * @param list2 the right side
     * @return a Similarity analysis
     */
    public Similarity compare(String name, List<T> list1, List<T> list2) {
        Map<T, Map<T, Similarity>> matrix = new HashMap<>();
        // Build all comparisons once
        for (T obj1 : list1) {
            HashMap<T, Similarity> mapI = new HashMap<>();
            matrix.put(obj1, mapI);
            for (T obj2 : list2) {
                Similarity sim = similarityEvaluator.eval(obj1, obj2);
                mapI.put(obj2, sim);
            }
        }
        return maximumMatch(name, matrix, list1, list2);
    }

    private Similarity maximumMatch(String name, Map<T, Map<T, Similarity>> matrix, List<T> list1, List<T> list2) {
        return Similarity.add(name, maximumMatchRec(matrix, list1, list2));
    }

    // Recursive
    private List<Similarity> maximumMatchRec(Map<T, Map<T, Similarity>> matrix, List<T> list1, List<T> list2) {
        if (list1.isEmpty() || list2.isEmpty()) {
            List<Similarity> loners = new ArrayList<>();
            for (T t : list1) {
                loners.add(new LeftLeafSimilarity<T>(sizer.size(t), t));
            }
            for (T t : list2) {
                loners.add(new RightLeafSimilarity<T>(sizer.size(t), t));
            }
            return loners;
        }
        List<Similarity> bestMatch = null;
        // Make first pair
        for (int i = 0; i < list1.size(); i++) {
            T obj1 = list1.get(i);
            List<T> ommitI = new ArrayList<>(list1);
            ommitI.remove(i);
            for (int j = 0; j < list2.size(); j++) {
                List<T> ommitJ = new ArrayList<>(list2);
                ommitJ.remove(j);
                T obj2 = list2.get(j);
                Similarity sim12 = matrix.get(obj1).get(obj2);
                Similarity matchIJ = sim12;

                // Match the rest of the fucking owl
                List<Similarity> subMatching = maximumMatchRec(matrix, ommitI, ommitJ);

                // mix initial Pair with submatching
                subMatching.add(matchIJ);

                if (bestMatch == null || totalSimilarity(subMatching) > totalSimilarity(bestMatch)) {
                    bestMatch = subMatching;
                }
            }
        }
        return bestMatch;
    }

    private double totalSimilarity(List<Similarity> input) {
        return input.stream().collect(Collectors.summingDouble(Similarity::similarity));
    }

    @AllArgsConstructor
    @Getter
    private static class TrucShowable implements Showable {
        private final int val;

        @Override
        public List<String> show(String prefix) {
            return Arrays.asList(prefix + Integer.toString(val));
        }

        @Override
        public String toString() {
            return String.join("\n", show(""));
        }

    }

    /**
     * Just a test
     * @param arg unused
     */
    public static void main(String[] arg) {
        UnitySizer<TrucShowable> intSizer = new UnitySizer<TrucShowable>();
        SimilarityEvaluator<TrucShowable> similarityEvaluator = new SimilarityEvaluator<TrucShowable>(intSizer, "truc") {

            @Override
            public Similarity eval(TrucShowable obj1, TrucShowable obj2) {
                double diff = Math.abs(obj1.val - obj2.val) / Math.max(1.0, Math.max(Math.abs(obj1.val), Math.abs(obj2.val)));
                return new Similarity(1 - diff, 1) {

                    @Override
                    public List<String[]> show(String prefix) {
                        List<String[]> list = new ArrayList<>();
                        list.add(new String[] {prefix + obj1, Double.toString(1 - diff), prefix + obj2});
                        return list;
                    }

                    @Override
                    public String toString() {
                        return "<" + similarity() + "=" + obj1 + " / " + obj2 + ">";
                    }

                };
            }

        };

        List<TrucShowable> list1 = new ArrayList<TrucShowable>() {
            private static final long serialVersionUID = 1L;

            {
                this.add(new TrucShowable(6));
                this.add(new TrucShowable(3));
                this.add(new TrucShowable(5));
            }
        };
        List<TrucShowable> list2 = new ArrayList<TrucShowable>() {
            private static final long serialVersionUID = 1L;
            {
                this.add(new TrucShowable(2));
                this.add(new TrucShowable(4));
                this.add(new TrucShowable(6));
            }
        };
        Similarity result = similarityEvaluator.maximumMatch(list1, list2);
        System.out.println(result.toString());
    }
}
