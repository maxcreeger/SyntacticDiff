package diff.similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import diff.complexity.Showable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Simple similarity (placeholder to present differences)
 */
@Getter
public class SimpleSimilarity extends Similarity {

    @Getter
    @AllArgsConstructor
    public static class ShowableString implements Showable {
        String content;

        @Override
        public List<String> show(String prefix) {
            List<String> list = new ArrayList<>();
            list.add(content);
            return list;
        }
    }

    private final ShowableString leftComment;
    private final String diff;
    private final ShowableString rightComment;

    public ShowableString showLeft() {
        return leftComment;
    }

    public ShowableString showRight() {
        return rightComment;
    }

    public List<Similarity> subSimilarities() {
        return new ArrayList<>();
    }

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
        this.leftComment = new ShowableString(leftComment);
        this.diff = diff;
        this.rightComment = new ShowableString(rightComment);
    }

    @Override
    public List<String[]> show(String prefix) {
        List<String[]> list = new ArrayList<>();
        list.add(new String[] {prefix + leftComment, diff, prefix + rightComment});
        return list;
    }

    @Override
    public <R> R accept(SimilarityVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.join("\n", show("").stream().flatMap(array -> Arrays.stream(array)).collect(Collectors.toList()));
    }

}
