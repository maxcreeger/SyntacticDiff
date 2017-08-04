package parser.syntaxtree.expression.statement.primitivetypes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.Whitespace;


/**
 * Represents an boolean literal like <code>false</code>.
 */
@Getter
@AllArgsConstructor
public class BooleanValue extends PrimitiveValue {

    private static final Pattern booleanPattern = Pattern.compile("(true)|(false)");

    private final boolean isTrue;

    /**
     * Attempts to build the primitive.
     * @param inputRef the mutable input text (is modified if the primitive is created)
     * @return optionally, the primitive
     */
    public static Optional<BooleanValue> build(AtomicReference<String> inputRef) {
        Matcher stringMatcher = booleanPattern.matcher(inputRef.get());
        if (stringMatcher.lookingAt()) {
            boolean val = Boolean.parseBoolean(stringMatcher.group(0));
            inputRef.set(inputRef.get().substring(stringMatcher.end()));
            Whitespace.skipWhitespaceAndComments(inputRef);
            return Optional.of(new BooleanValue(val));
        }
        return Optional.empty();
    }

    @Override
    public List<String> show(String prefix) {
        return Arrays.asList(prefix + (isTrue ? "true" : "false"));
    }

    @Override
    public <T> T visit(PrimitiveVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
