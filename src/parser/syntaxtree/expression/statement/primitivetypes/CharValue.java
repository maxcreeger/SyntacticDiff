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
 * Represents a char literal like <code>'c'</code>.
 */
@Getter
@AllArgsConstructor
public class CharValue extends PrimitiveValue {


    private static final Pattern charPattern = Pattern.compile("'(\\\\?.)'");

    private final String charContent;

    /**
     * Attempts to build the primitive.
     * @param inputRef the mutable input text (is modified if the primitive is created)
     * @return optionally, the primitive
     */
    public static Optional<CharValue> build(AtomicReference<String> inputRef) {
        Matcher stringMatcher = charPattern.matcher(inputRef.get());
        if (stringMatcher.lookingAt()) {
            inputRef.set(inputRef.get().substring(stringMatcher.end()));
            Whitespace.skipWhitespaceAndComments(inputRef);
            return Optional.of(new CharValue(stringMatcher.group(1)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<String> show(String prefix) {
        return Arrays.asList(prefix + "'" + charContent + "'");
    }

    @Override
    public <T> T visit(PrimitiveVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
