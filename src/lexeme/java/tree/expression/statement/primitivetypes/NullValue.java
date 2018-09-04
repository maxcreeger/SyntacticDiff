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
 * Represents the <code>null</code> litteral.
 */
@Getter
@AllArgsConstructor
public class NullValue extends PrimitiveValue {

    private static final Pattern intPattern = Pattern.compile("null");

    private final CodeLocation location;

    /**
     * Attempts to build the primitive.
     * @param inputRef the mutable input text (is modified if the primitive is created)
     * @return optionally, the primitive
     */
    public static Optional<NullValue> build(CodeBranch inputRef) {
        CodeBranch fork = inputRef.fork();

        Matcher stringMatcher = intPattern.matcher(fork.getRest());
        if (stringMatcher.lookingAt()) {
            fork.advance(stringMatcher.end());
            JavaWhitespace.skipWhitespaceAndComments(fork);
            return Optional.of(new NullValue(fork.commit()));
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
