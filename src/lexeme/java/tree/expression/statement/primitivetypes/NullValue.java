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
 * Represents the <code>null</code> litteral.
 */
@Getter
@AllArgsConstructor
public class NullValue extends PrimitiveValue {

    private static final Pattern intPattern = Pattern.compile("null");

    /**
     * Attempts to build the primitive.
     * @param inputRef the mutable input text (is modified if the primitive is created)
     * @return optionally, the primitive
     */
    public static Optional<NullValue> build(AtomicReference<String> inputRef) {
        Matcher stringMatcher = intPattern.matcher(inputRef.get());
        if (stringMatcher.lookingAt()) {
            inputRef.set(inputRef.get().substring(stringMatcher.end()));
            JavaWhitespace.skipWhitespaceAndComments(inputRef);
            return Optional.of(new NullValue());
        }
        return Optional.empty();
    }

    @Override
    public List<String> show(String prefix) {
        return Arrays.asList(">null<");
    }

    @Override
    public <T> T visit(PrimitiveVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getWord() {
        return ">null<";
    }

    @Override
    public String toString() {
        return ">null<";
    }

}
