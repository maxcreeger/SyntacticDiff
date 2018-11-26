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
 * Represents an boolean literal like <code>false</code>.
 */
@Getter
public class BooleanValue extends PrimitiveValue {

	private static final Pattern booleanPattern = Pattern.compile("(true)|(false)");

	private final boolean isTrue;

	public BooleanValue(boolean isTrue, CodeLocation location) {
		super(location);
		this.isTrue = isTrue;
	}

	/**
	 * Attempts to build the primitive.
	 * 
	 * @param inputRef
	 *            the mutable input text (is modified if the primitive is
	 *            created)
	 * @return optionally, the primitive
	 */
	public static Optional<BooleanValue> build(CodeBranch inputRef) {
		CodeBranch fork = inputRef.fork();
		Matcher stringMatcher = booleanPattern.matcher(fork.getRest());
		if (stringMatcher.lookingAt()) {
			boolean val = Boolean.parseBoolean(stringMatcher.group(0));
			fork.advance(stringMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(fork);
			return Optional.of(new BooleanValue(val, fork.commit()));
		}
		return Optional.empty();
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
		return SyntacticSettings.red() + SyntacticSettings.bold() + (isTrue ? "true" : "false") + SyntacticSettings.reset();
	}

	@Override
	public String toString() {
		return getWord();
	}

}
