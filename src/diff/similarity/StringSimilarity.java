package diff.similarity;

import java.util.ArrayList;
import java.util.List;

import diff.similarity.SimpleSimilarity.ShowableString;

/**
 * Evaluates a similarity metric between two strings.
 */
public final class StringSimilarity extends LeafSimilarity<ShowableString> {

	StringSimilarity(double same, int amount, ShowableString str1, ShowableString str2) {
		super("String", same, amount, str1, str2);
	}

	@Override
	public List<String[]> show(String prefix) {
		List<String[]> result = new ArrayList<>(1);
		result.add(new String[] { prefix + obj1, Double.toString(similarity()), prefix + obj2 });
		return result;
	}

}
