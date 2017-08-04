package parser.syntaxtree.expression.statement;

import java.util.ArrayList;
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
 * Represents a '.' statement acting on another statement like <code>new Object().toString()</code>. <br>
 * The dot may represent field access or method invocation, or Class name hierarchy (<code>new Map.Entry()</code>)
 */
@Getter
@AllArgsConstructor
public class ChainedAccess extends Statement {

    private static final Pattern beginInvocation = Pattern.compile("\\.");

    private final Statement source;
    private final Statement chainedAction;

    /**
     * Attempts to build a {@link ChainedAccess} from a mutable input String.
     * @param source an identified subject of the access
     * @param inputRef the input string. If a match is found, its text representation is removed from the input string
     * @return optionally, a {@link ChainedAccess}
     */
    public static Optional<ChainedAccess> build(Statement source, AtomicReference<String> inputRef) {
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get());

        // Expect a '.'
        if (!startChaining(defensiveCopy)) {
            return Optional.empty();
        }

        // Attempt to find method invocation
        Optional<MethodInvocation> optionalMethodInvocation = MethodInvocation.build(defensiveCopy);
        if (optionalMethodInvocation.isPresent()) {
            inputRef.set(defensiveCopy.get());
            return Optional.of(new ChainedAccess(source, optionalMethodInvocation.get()));
        }

        // Attempt to find field access
        Optional<VariableReference> optionalVarRef = VariableReference.build(defensiveCopy);
        if (optionalVarRef.isPresent()) {
            inputRef.set(defensiveCopy.get());
            return Optional.of(new ChainedAccess(source, optionalVarRef.get()));
        }

        // Nothing found
        return Optional.empty();
    }

    private static boolean startChaining(AtomicReference<String> input) {
        Matcher beginMatcher = beginInvocation.matcher(input.get());
        if (!beginMatcher.lookingAt()) {
            return false;
        } else {
            input.set(input.get().substring(beginMatcher.end()));
            Whitespace.skipWhitespaceAndComments(input);
            return true;
        }
    }

    @Override
    public boolean isAssignable() {
        return chainedAction.isAssignable();
    }

    // Display

    @Override
    public List<String> show(String prefix) {
        final List<String> sourceShow = source.show("");
        final List<String> actionShow = chainedAction.show("");
        if (sourceShow.size() == 1) {
            if (actionShow.size() == 1) {
                return Arrays.asList(prefix + sourceShow.get(0) + "." + actionShow.get(0));
            } else {
                List<String> result = new ArrayList<>();
                result.add(prefix + sourceShow.get(0) + "." + actionShow.get(0));
                for (int i = 1; i < actionShow.size(); i++) {
                    result.add(prefix + "a  " + actionShow.get(i));
                }
                return result;
            }
        } else {
            List<String> result = new ArrayList<>();
            for (int i = 0; i < sourceShow.size(); i++) {
                result.add(prefix + sourceShow.get(i));
            }
            for (int i = 0; i < actionShow.size(); i++) {
                if (i == 0) {
                    result.add(prefix + "." + actionShow.get(0));
                } else {
                    result.add(prefix + " " + actionShow.get(i));
                }
            }
            return result;
        }
    }

    @Override
    public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
