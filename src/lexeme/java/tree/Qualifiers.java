package lexeme.java.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import diff.complexity.Showable;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import settings.SyntacticSettings;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;
import tokenizer.tokens.Symbol;

/**
 * A Qualifier (private, public, abstract, synchronized etc).
 */
@Getter
@AllArgsConstructor
public class Qualifiers implements Showable, Symbol<JavaGrammar> {

	public static enum JavaQualifier {
		/** Private-visibility for method, class or field. */
		PRIVATE,
		/** Protected-visibility for method, class or field. */
		PROTECTED,
		/** Public-visibility for method, class or field. */
		PUBLIC,
		/** Package-visibility for method, class or field. */
		PACKAGE,
		/** Static method, class or field. */
		STATIC,
		/** Abstract method or class. */
		ABSTRACT,
		/** Final method, class or field. */
		FINAL,
		/** Transient field. */
		TRANSIENT,
		/** Volatile field. */
		VOLATILE,
		/** Synchronized method or field. */
		SYNCHRONIZED;

		/**
		 * Makes a {@link QualifierDetector} which can scan a string do detect
		 * this qualifier.
		 * 
		 * @return a {@link QualifierDetector}
		 */
		public QualifierDetector makeDetector() {
			return new QualifierDetector(name().toLowerCase());
		}
	}

	JavaQualifier qualifier;
	CodeLocation location;

	private static List<QualifierDetector> detectors = new ArrayList<>();
	static {
		for (JavaQualifier qualifier : JavaQualifier.values()) {
			detectors.add(qualifier.makeDetector());
		}
	}

	/**
	 * Helper method, will scan the input String and output <b>all</b>
	 * qualifiers at the start of it. <br>
	 * The String is mutated as the qualifiers will be removed!
	 * 
	 * @param input
	 *            the input mutable string
	 * @return a list of any qualifier found (never null, at least empty)
	 */
	public static List<Qualifiers> searchQualifiers(CodeBranch input) {
		CodeBranch fork = input.fork();
		List<Qualifiers> found = new ArrayList<>();
		while (true) {
			Optional<Qualifiers> optional = Qualifiers.detect(fork);
			if (optional.isPresent()) {
				found.add(optional.get());
				continue;
			}
			break;
		}
		if (!found.isEmpty()) {
			fork.commit(); // It's ok to lose this Location
		}
		return found;
	}

	/**
	 * Helper method, will scan the input String and output <b>ONE</b> qualifier
	 * at the start of it. <br>
	 * The String is mutated as the qualifier will be removed!
	 * 
	 * @param input
	 *            the input mutable string
	 * @return optionally, a qualifier
	 */
	public static Optional<Qualifiers> detect(CodeBranch input) {
		for (QualifierDetector qualifierDetector : detectors) {
			Optional<Qualifiers> optional = qualifierDetector.build(input);
			if (optional.isPresent()) {
				return optional;
			}
		}
		return Optional.empty();
	}

	/**
	 * Detector class for qualifiers in String inputs.
	 */
	public static class QualifierDetector {
		private final Pattern pattern;

		/**
		 * Build a qualifier detector looking like the input word.
		 * 
		 * @param keyword
		 *            the qualifier text
		 */
		public QualifierDetector(String keyword) {
			this.pattern = Pattern.compile("^" + keyword);
		}

		/**
		 * Scans the input String and output <b>ONE</b> qualifier at the start
		 * of it. <br>
		 * The String is mutated as the qualifier will be removed!
		 * 
		 * @param input
		 *            the input mutable string
		 * @return optionally, a qualifier
		 */
		public Optional<Qualifiers> build(CodeBranch input) {
			CodeBranch qualifCode = input.fork();
			Matcher matcher = pattern.matcher(qualifCode.getRest());
			if (matcher.lookingAt()) {
				qualifCode.advance(matcher.end());
				String matchedKeyword = matcher.group(0).toUpperCase();
				JavaWhitespace.skipWhitespaceAndComments(qualifCode);
				return Optional.of(new Qualifiers(JavaQualifier.valueOf(matchedKeyword), qualifCode.commit()));
			} else {
				return Optional.empty();
			}
		}
	}

	/**
	 * Compares a {@link List} of qualifiers to another.
	 * 
	 * @param qualifiersA
	 *            a list of qualifiers
	 * @param qualifiersB
	 *            a list of qualifiers
	 * @return <code>true</code> if both lists are the same (order does not
	 *         matter), <code>false</code> otherwise
	 */
	public static boolean compare(List<Qualifiers> qualifiersA, List<Qualifiers> qualifiersB) {
		if (qualifiersA.size() != qualifiersB.size()) {
			return false;
		}
		for (int num = 0; num < qualifiersA.size(); num++) {
			Qualifiers qualA = qualifiersA.get(num);
			Qualifiers qualB = qualifiersB.get(num);
			if (qualA != qualB) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return SyntacticSettings.cyan() + qualifier.name().toLowerCase() + SyntacticSettings.reset();
	}

	/**
	 * Displays a series of qualifier nicely in a single line.
	 * 
	 * @param qualifiers
	 *            a {@link List} of {@link Qualifiers}
	 * @return a nice String like "public static final synchronized";
	 */
	public static String toString(List<Qualifiers> qualifiers) {
		return String.join(" ", qualifiers.stream().map(Qualifiers::toString).collect(Collectors.toList()));
	}

	/**
	 * Displays a series of qualifier all as individual lines.
	 * 
	 * @param qualifiers
	 *            a {@link List} of {@link Qualifiers}
	 * @return a nice String like "public static final synchronized";
	 */
	public static List<String> fullBreakdown(List<Qualifiers> qualifiers) {
		return qualifiers.stream().map(Qualifiers::toString).collect(Collectors.toList());
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		return Arrays.asList(toString());
	}

	@Override
	public String getSymbol() {
		return toString().toLowerCase();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Qualifiers) {
			Qualifiers other = (Qualifiers) obj;
			return this.getQualifier().equals(other.getQualifier());
		} else {
			return false;
		}
	}
}
