package diff.similarity;

import java.util.ArrayList;
import java.util.List;

import diff.similarity.SimpleSimilarity.ShowableString;

/**
 * Evaluates a similarity metric between two strings.
 */
public final class StringSimilarity extends LeafSimilarity<ShowableString> {

    StringSimilarity(double same, int amount, String str1, String str2) {
        super(same, amount, new ShowableString(str1), new ShowableString(str2));
    }

    @Override
    public List<String[]> show(String prefix) {
        List<String[]> result = new ArrayList<>(1);
        result.add(new String[] {prefix + obj1, Double.toString(similarity()), prefix + obj2});
        return result;
    }

}
