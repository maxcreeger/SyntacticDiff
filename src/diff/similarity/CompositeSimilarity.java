package diff.similarity;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregates several {@link Similarity} analysis in a single one.
 */
public class CompositeSimilarity extends Similarity {

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
}
