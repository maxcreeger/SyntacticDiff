package lexeme.java.tree.expression.statement.primitivetypes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an integer literal like <code>1024</code>.
 */
@Getter
@AllArgsConstructor
public class IntegerValue extends PrimitiveValue {

    private static final Pattern decimalPattern = Pattern.compile("(-?[0-9]+)([Ll]?)");

    private final int decimalValue;
    private boolean isLong;

    /**
     * Attempts to build the primitive.
     * @param inputRef the mutable input text (is modified if the primitive is created)
     * @return optionally, the primitive
     */
    public static Optional<IntegerValue> build(AtomicReference<String> inputRef) {
        Matcher stringMatcher = decimalPattern.matcher(inputRef.get());
        if (stringMatcher.lookingAt()) {
            int val = Integer.parseInt(stringMatcher.group(1));
            boolean asLong = !stringMatcher.group(2).isEmpty();
            inputRef.set(inputRef.get().substring(stringMatcher.end()));
            JavaWhitespace.skipWhitespaceAndComments(inputRef);
            return Optional.of(new IntegerValue(val, asLong));
        }
        return Optional.empty();
    }

    @Override
    public List<String> show(String prefix) {
        return Arrays.asList(prefix + decimalValue + (isLong ? "L" : ""));
    }

    @Override
    public <T> T visit(PrimitiveVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getWord() {
        return decimalValue + (isLong ? "L" : "");
    }

    @Override
    public String toString() {
        return getWord();
    }

}
