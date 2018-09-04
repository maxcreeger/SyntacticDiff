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
 * Represents a char literal like <code>'c'</code>.
 */
@Getter
@AllArgsConstructor
public class CharValue extends PrimitiveValue {


    private static final Pattern charPattern = Pattern.compile("'(\\\\?.)'");

    private final String charContent;
    private final CodeLocation location;

    /**
     * Attempts to build the primitive.
     * @param inputRef the mutable input text (is modified if the primitive is created)
     * @return optionally, the primitive
     */
    public static Optional<CharValue> build(CodeBranch inputRef) {
        CodeBranch fork = inputRef.fork();
        Matcher stringMatcher = charPattern.matcher(fork.getRest());
        if (stringMatcher.lookingAt()) {
            fork.advance(stringMatcher.end());
            JavaWhitespace.skipWhitespaceAndComments(inputRef);
            return Optional.of(new CharValue(stringMatcher.group(1), fork.commit()));
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

    @Override
    public String getWord() {
        return "'" + charContent + "'";
    }

    @Override
    public String toString() {
        return getWord();
    }
}
