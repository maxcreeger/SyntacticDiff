package lexeme.java.tree.expression.statement.primitivetypes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents an double literal like <code>1024.05</code>.
 */
@Getter
@AllArgsConstructor
public class DoubleValue extends PrimitiveValue {

    private static final Pattern numPattern1 = Pattern.compile("(-?[0-9]+\\.[0-9]*(e-?[0-9]+)?)([dDfF]?)");
    private static final Pattern numPattern2 = Pattern.compile("(-?\\.[0-9]+(e-?[0-9]+)?)([dDfF]?)");

    private final double doubleValue;
    private final boolean isDouble;
    CodeLocation location;

    /**
     * Attempts to build the primitive.
     * @param inputRef the mutable input text (is modified if the primitive is created)
     * @return optionally, the primitive
     */
    public static Optional<DoubleValue> build(CodeBranch inputRef) {
        CodeBranch fork = inputRef.fork();

        Matcher stringMatcher = numPattern1.matcher(fork.getRest());
        if (stringMatcher.lookingAt()) {
            double val = Double.parseDouble(stringMatcher.group(0));
            boolean isDouble = stringMatcher.group(2).isEmpty() || stringMatcher.group(2).toLowerCase().equals("d");
            fork.advance(stringMatcher.end());
            JavaWhitespace.skipWhitespaceAndComments(fork);
            return Optional.of(new DoubleValue(val, isDouble, fork.commit()));
        }
        stringMatcher = numPattern2.matcher(fork.getRest());
        if (stringMatcher.lookingAt()) {
            double val = Double.parseDouble(stringMatcher.group(0));
            boolean isDouble = stringMatcher.group(2).isEmpty() || stringMatcher.group(2).toLowerCase().equals("d");
            fork.advance(stringMatcher.end());
            JavaWhitespace.skipWhitespaceAndComments(fork);
            return Optional.of(new DoubleValue(val, isDouble, fork.commit()));
        }
        return Optional.empty();
    }

    @Override
    public List<String> show(String prefix) {
        return Arrays.asList(getWord());
    }

    @Override
    public <T> T visit(PrimitiveVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getWord() {
        return doubleValue + (isDouble ? "d" : "f");
    }

    @Override
    public String toString() {
        return getWord();
    }

}
