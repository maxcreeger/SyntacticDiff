package hmi;

import org.junit.Test;

import lexeme.java.JavaTokenizer;
import lexeme.java.tree.Root;
import settings.SyntacticSettings;

public class TestHMI {

	/**
	 * Javadoc!
	 */
	@Test
	public void testTokenize() {
		SyntacticSettings.useColorsInConsole(true);
		Root javaTokenizer1 = new JavaTokenizer("resources/JavaTokenizer.java").tokenize();
		System.out.println(String.join("\n", javaTokenizer1.fullBreakdown("TK1> ")));
		SyntacticSettings.useColorsInConsole(false);
	}

}
