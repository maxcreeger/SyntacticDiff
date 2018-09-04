package lexeme.java.tree.expression.statement;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * An object's reference to itself or to its super self.<br>
 * May match <code>this</code> or <code>super</code>.
 */
@Getter
@AllArgsConstructor
public class SelfReference extends Statement {

    private static final Pattern thisPattern = Pattern.compile("this");
    private static final Pattern superPattern = Pattern.compile("super");

    private final boolean isThis;
    private final CodeLocation location;

    @Override
    public boolean isAssignable() {
        return false;
    }

    /**
     * Attempts to build a {@link SelfReference}.
     * @param input the input string. Is mutated if a self reference is found
     * @return optionally, a self reference
     */
    public static Optional<SelfReference> build(CodeBranch input) {
        CodeBranch fork = input.fork();
        Matcher thisMatcher = thisPattern.matcher(fork.getRest());
        if (thisMatcher.lookingAt()) {
            fork.advance(thisMatcher.end());
            return Optional.of(new SelfReference(true, fork.commit()));
        }

        Matcher superMatcher = superPattern.matcher(fork.getRest());
        if (superMatcher.lookingAt()) {
            fork.advance(superMatcher.end());
            return Optional.of(new SelfReference(false, fork.commit()));
        }

        return Optional.empty();
    }

    @Override
    public List<String> show(String prefix) {
        return Arrays.asList(isThis ? "this" : "super");
    }

    @Override
    public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
