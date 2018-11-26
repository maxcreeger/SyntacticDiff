package lexeme.java.tree.expression.statement.primitivetypes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents an integer literal like <code>1024</code>.
 */
@Getter
public class IntegerValue extends PrimitiveValue {
	private static final Pattern decimalPattern = Pattern.compile("(-?[0-9]+)([Ll]?)");

	private final int integerValue;
	private final boolean isLong;

	public IntegerValue(int integerValue, boolean isLong, CodeLocation location) {
		super(location);
		this.integerValue = integerValue;
		this.isLong = isLong;
	}

	/**
	 * Attempts to build the primitive.
	 * 
	 * @param inputRef
	 *            the mutable input text (is modified if the primitive is
	 *            created)
	 * @return optionally, the primitive
	 */
	public static Optional<IntegerValue> build(CodeBranch inputRef) {
		CodeBranch fork = inputRef.fork();

		Matcher stringMatcher = decimalPattern.matcher(fork.getRest());
		if (stringMatcher.lookingAt()) {
			int val = Integer.parseInt(stringMatcher.group(1));
			boolean asLong = !stringMatcher.group(2).isEmpty();
			fork.advance(stringMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(fork);
			return Optional.of(new IntegerValue(val, asLong, fork.commit()));
		}
		return Optional.empty();
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		return Arrays.asList(prefix + integerValue + (isLong ? "L" : ""));
	}

	@Override
	public <T> T visit(PrimitiveVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String getWord() {
		return integerValue + (isLong ? "L" : "");
	}

	@Override
	public String toString() {
		return getWord();
	}

}
