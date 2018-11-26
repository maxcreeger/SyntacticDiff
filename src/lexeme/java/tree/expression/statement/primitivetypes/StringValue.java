package lexeme.java.tree.expression.statement.primitivetypes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lombok.Getter;
import settings.SyntacticSettings;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * A {@link String} primitive such as "toto".
 */
@Getter
public class StringValue extends PrimitiveValue {

	private static final char DOUBLE_QUOTE = '\"';
	private static final Pattern stringPattern = Pattern.compile("\\\"[\\s\\S]*?([^\\\\]?\\\")");
	private static final Pattern quotePattern = Pattern.compile("\\\"");
	private static final Pattern endOfStringPattern = Pattern.compile("[^\\\\]?\"");
	private static final Pattern endOfLinePattern = Pattern.compile("\\n");
	private final String stringContent;

	public StringValue(String stringContent, CodeLocation location) {
		super(location);
		this.stringContent = stringContent;
	}

	/**
	 * Attempts to build the primitive.
	 * 
	 * @param inputRef
	 *            the mutable input text (is modified if the primitive is
	 *            created)
	 * @return optionally, the primitive
	 */
	public static Optional<StringValue> build(CodeBranch inputRef) {
		CodeBranch fork = inputRef.fork();

		Matcher stringMatcher = stringPattern.matcher(fork.getRest());
		if (stringMatcher.lookingAt()) {
			fork.advance(stringMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(fork);
			String stringWithQuotes = stringMatcher.group();
			return Optional.of(new StringValue(stringWithQuotes.substring(1, stringWithQuotes.length() - 1), fork.commit()));
		} else {
			return Optional.empty();
		}
	}

	private static Optional<PrimitiveValue> matchString(CodeBranch inputRef) {
		CodeBranch fork = inputRef.fork();

		Matcher start = quotePattern.matcher(fork.getRest());
		if (!start.lookingAt()) {
			return Optional.empty();
		}
		StringBuilder content = new StringBuilder();
		fork.advance(start.end());

		while (true) {
			Matcher endOfString = endOfStringPattern.matcher(fork.getRest());
			if (endOfString.lookingAt()) {
				fork.advance(endOfString.end());
				break; // End of String definition
			}

			Matcher endOfLine = endOfLinePattern.matcher(fork.getRest());
			if (endOfLine.lookingAt()) {
				return Optional.empty(); // Illegal end of line inside String definition
			}

			// Advance 1 char
			char advance = fork.getRest().charAt(0);
			content.append(advance);
			fork.advance(1);
		}

		JavaWhitespace.skipWhitespaceAndComments(fork);
		return Optional.of(new StringValue(content.toString(), fork.commit()));
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		return Arrays.asList(prefix + getWord());
	}

	@Override
	public <T> T visit(PrimitiveVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String getWord() {
		return SyntacticSettings.blue() + SyntacticSettings.bold() + DOUBLE_QUOTE + stringContent + DOUBLE_QUOTE + SyntacticSettings.reset();
	}

	@Override
	public String toString() {
		return getWord();
	}

}
