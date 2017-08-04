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
 * Represents an double literal like <code>1024.05</code>.
 */
@Getter
@AllArgsConstructor
public class DoubleValue extends PrimitiveValue {

    private static final Pattern numPattern1 = Pattern.compile("(-?[0-9]+\\.[0-9]*(e-?[0-9]+)?)([dDfF]?)");
    private static final Pattern numPattern2 = Pattern.compile("(-?\\.[0-9]+(e-?[0-9]+)?)([dDfF]?)");

    private final double doubleValue;
    private final boolean isDouble;

    /**
     * Attempts to build the primitive.
     * @param inputRef the mutable input text (is modified if the primitive is created)
     * @return optionally, the primitive
     */
    public static Optional<DoubleValue> build(AtomicReference<String> inputRef) {
        Matcher stringMatcher = numPattern1.matcher(inputRef.get());
        if (stringMatcher.lookingAt()) {
            double val = Double.parseDouble(stringMatcher.group(0));
            boolean isDouble = stringMatcher.group(2).isEmpty() || stringMatcher.group(2).toLowerCase().equals("d");
            inputRef.set(inputRef.get().substring(stringMatcher.end()));
            Whitespace.skipWhitespaceAndComments(inputRef);
            return Optional.of(new DoubleValue(val, isDouble));
        }
        stringMatcher = numPattern2.matcher(inputRef.get());
        if (stringMatcher.lookingAt()) {
            double val = Double.parseDouble(stringMatcher.group(0));
            boolean isDouble = stringMatcher.group(2).isEmpty() || stringMatcher.group(2).toLowerCase().equals("d");
            inputRef.set(inputRef.get().substring(stringMatcher.end()));
            Whitespace.skipWhitespaceAndComments(inputRef);
            return Optional.of(new DoubleValue(val, isDouble));
        }
        return Optional.empty();
    }

    @Override
    public List<String> show(String prefix) {
        return Arrays.asList(prefix + doubleValue + (isDouble ? "d" : "f"));
    }

    @Override
    public <T> T visit(PrimitiveVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
