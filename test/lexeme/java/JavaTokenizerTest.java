package lexeme.java;

import java.util.List;

import org.junit.Test;

import diff.similarity.Similarity;
import diff.similarity.evaluator.RootSimilarityEvaluator;
import lexeme.java.tree.Root;
import settings.SyntacticSettings;

/**
 * test class.
 */

public class JavaTokenizerTest {

	/**
	 * Javadoc!
	 */
	@Test
	public void testTokenize() {
		SyntacticSettings.useColorsInConsole(true);
		Root javaTokenizer1 = new JavaTokenizer("resources/JavaTokenizer.java").tokenize();
		System.out.println(String.join("\n", javaTokenizer1.show("TK1> ")));
		SyntacticSettings.useColorsInConsole(false);
	}

	/**
	 * Javadoc!
	 */
	@Test
	public void testSimilarity() {
		Root javaTokenizer1 = new JavaTokenizer("resources/JavaTokenizer.java").tokenize();
		Root javaTokenizer2 = new JavaTokenizer("resources/JavaTokenizer2.java").tokenize();

		Similarity similarity = RootSimilarityEvaluator.INSTANCE.eval(javaTokenizer1, javaTokenizer2);
		System.out.println("Similarity : " + Math.round(similarity.similarity() * 100) + "%");
		final List<String[]> show = similarity.show("");
		for (String[] strings : show) {
			String formatted = String.format("%0$-" + 60 + "." + 60 + "s (%0$-5.5s)  %s", strings[0], strings[1], strings[2]);
			System.out.println(formatted);
		}
	}

}
