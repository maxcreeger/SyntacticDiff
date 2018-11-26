package diff;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Computes the Levenstein distance between two strings.
 */
@RequiredArgsConstructor
public class Levenshtein {

	private final Map<Input, Integer> cache = new HashMap<>();

	private final String a;
	private final String b;

	/**
	 * Test class
	 * 
	 * @param args
	 *            useless
	 */
	public static void main(String[] args) {
		Levenshtein lev = new Levenshtein("Hello", "hullo!");
		int dist = lev.computeDistance();
		System.out.println("dist = " + dist);
	}

	/**
	 * Computes the distance between the two loaded strings
	 * 
	 * @return the distance
	 */
	public int computeDistance() {
		int i = a.length() - 1;
		int j = b.length() - 1;
		return computeRec(i, j);
	}

	private int computeRec(int i, int j) {
		Integer cachedValue = cache.get(new Input(i, j));
		if (cachedValue != null) {
			return cachedValue;
		}
		int val;
		if (Math.min(i, j) == 0) {
			if (Math.max(i, j) == 0) {
				val = (a.charAt(i) != b.charAt(j) ? 1 : 0); // only 1 char
			} else {
				val = Math.max(i, j);
			}
		} else {
			int lev1 = computeRec(i - 1, j) + 1;
			int lev2 = computeRec(i, j - 1) + 1;
			int lev3 = computeRec(i - 1, j - 1) + (a.charAt(i) != b.charAt(j) ? 1 : 0);
			val = Math.min(Math.min(lev1, lev2), lev3);
		}
		cache.put(new Input(i, j), val);
		return val;
	}

	@AllArgsConstructor
	@Getter
	@EqualsAndHashCode
	@ToString
	private static class Input {
		private final int i;
		private final int j;
	}

}
