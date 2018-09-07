package diff.similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import diff.complexity.Showable;

/**
 * Aggregates several {@link Similarity} analysis in a single one.
 */
public class CompositeSimilarity extends Similarity {

    public static final class AggregateShowable implements Showable {
        private final List<Showable> allLeft;

        AggregateShowable(List<Showable> allLeft) {
            this.allLeft = allLeft;
        }

        @Override
        public List<String> show(String prefix) {
            List<String> result = new ArrayList<>();
            for (Showable showable : allLeft) {
                if (showable != null) {
                    result.addAll(showable.show(prefix));
                }
            }
            return result;
        }
    }

    final String name;
    final Similarity[] contents;

    /**
     * Builds a Similarity analysis out of several smaller ones. under a common function.
     * 
     * @param name name of the aggregate
     * @param same the similarity
     * @param amount the size
     * @param contents the contents
     */
    public CompositeSimilarity(String name, double same, int amount, Similarity[] contents) {
        super(same, amount);
        this.name = name;
        this.contents = contents;
    }

    @Override
    public List<String[]> show(String prefix) {
        List<String[]> result = new ArrayList<>();
        String header = "(" + name + ")";
        final String[] headerLine = new String[] {header, Double.toString(similarity()), header};
        result.add(headerLine);
        for (int s = 0; s < contents.length; s++) {
            Similarity similarity = contents[s];
            String separator = s < contents.length - 1 ? "|  " : "   ";
            List<String[]> show = similarity.show("");
            for (int i = 0; i < show.size(); i++) {
                String pref;
                if (i == 0) {
                    pref = "+——";
                } else {
                    pref = separator;
                }
                String[] strings = show.get(i);
                result.add(new String[] {pref + strings[0], strings[1], pref + strings[2]});
            }
        }
        return result;
    }

    /**
     * Creates a new Similarity, with this content plus the new entry.
     * 
     * @param newSimilarity an other similarity to add
     * @return a new similarity containing this and the other
     */
    public Similarity plus(Similarity newSimilarity) {
        List<Similarity> asList = new ArrayList<Similarity>();
        for (Similarity previous : contents) {
            if (previous != null) {
                asList.add(previous);
            }
        }
        asList.add(newSimilarity);
        return Similarity.add(name, asList);
    }

    @Override
    public <T> T accept(SimilarityVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Showable showLeft() {
        final List<Showable> allLeft = Arrays.stream(contents).map(Similarity::showLeft).collect(Collectors.toList());
        return new AggregateShowable(allLeft);
    }

    @Override
    public Showable showRight() {
        final List<Showable> allLeft = Arrays.stream(contents).map(Similarity::showRight).collect(Collectors.toList());
        return new AggregateShowable(allLeft);
    }

    @Override
    public List<Similarity> subSimilarities() {
        return Arrays.asList(contents);
    }
}
