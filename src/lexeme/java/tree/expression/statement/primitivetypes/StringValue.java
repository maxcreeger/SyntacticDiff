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
import settings.SyntacticSettings;

/**
 * A {@link String} primitive such as "toto".
 */
@Getter
@AllArgsConstructor
public class StringValue extends PrimitiveValue {

    private static final char DOUBLE_QUOTE = '\"';
    private static final Pattern stringPattern = Pattern.compile("\\\"[\\s\\S]*?([^\\\\]?\\\")");
    private static final Pattern quotePattern = Pattern.compile("\\\"");
    private static final Pattern endOfStringPattern = Pattern.compile("[^\\\\]?\"");
    private static final Pattern endOfLinePattern = Pattern.compile("\\n");
    private final String stringContent;

    /**
     * Attempts to build the primitive.
     * @param inputRef the mutable input text (is modified if the primitive is created)
     * @return optionally, the primitive
     */
    public static Optional<StringValue> build(AtomicReference<String> inputRef) {
        Matcher stringMatcher = stringPattern.matcher(inputRef.get());
        if (stringMatcher.lookingAt()) {
            inputRef.set(inputRef.get().substring(stringMatcher.end()));
            JavaWhitespace.skipWhitespaceAndComments(inputRef);
            String stringWithQuotes = stringMatcher.group();
            return Optional.of(new StringValue(stringWithQuotes.substring(1, stringWithQuotes.length() - 1)));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<PrimitiveValue> matchString(AtomicReference<String> inputRef) {
        Matcher start = quotePattern.matcher(inputRef.get());
        if (!start.lookingAt()) {
            return Optional.empty();
        }
        StringBuilder content = new StringBuilder();
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get().substring(start.end()));

        while (true) {
            Matcher endOfString = endOfStringPattern.matcher(defensiveCopy.get());
            if (endOfString.lookingAt()) {
                defensiveCopy.set(defensiveCopy.get().substring(endOfString.end()));
                break; // End of String definition
            }

            Matcher endOfLine = endOfLinePattern.matcher(defensiveCopy.get());
            if (endOfLine.lookingAt()) {
                return Optional.empty(); // Illegal end of line inside String definition
            }

            // Advance 1 char
            char advance = defensiveCopy.get().charAt(0);
            content.append(advance);
            defensiveCopy.set(defensiveCopy.get().substring(1));
        }

        // commit
        inputRef.set(defensiveCopy.get());
        JavaWhitespace.skipWhitespaceAndComments(defensiveCopy);
        return Optional.of(new StringValue(content.toString()));
    }

    @Override
    public List<String> show(String prefix) {
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
