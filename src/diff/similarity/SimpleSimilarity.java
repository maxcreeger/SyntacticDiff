package diff.similarity;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple similarity (placeholder to present differences)
 */
public class SimpleSimilarity extends Similarity {

    private final String leftComment;
    private final String diff;
    private final String rightComment;

    /**
     * Builds a {@link SimpleSimilarity} using messages for left & right sides
     * @param same similarity metric
     * @param amount sizing metric
     * @param leftComment left-side message
     * @param diff difference message (1, 0, ...)
     * @param rightComment right-side message
     */
    public SimpleSimilarity(double same, int amount, String leftComment, String diff, String rightComment) {
        super(same, amount);
        this.leftComment = leftComment;
        this.diff = diff;
        this.rightComment = rightComment;
    }

    @Override
    public List<String[]> show(String prefix) {
        List<String[]> list = new ArrayList<>();
        list.add(new String[] {prefix + leftComment, diff, prefix + rightComment});
        return list;
    }

}
