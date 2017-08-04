package parser.syntaxtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.expression.statement.Statement;
import parser.tokens.Parenthesis;

/**
 * Represents parameters that are passed during a method invocation.
 */
@AllArgsConstructor
public class ParameterPassing implements Showable {

    private static final Pattern separatorPattern = Pattern.compile(",");

    @Getter
    private final List<Statement> parameters;

    /**
     * Attempts to build a {@link List} of Statements that are passed as method parameters inside parenthesis (objA, objB).
     * @param inputRef a mutable String, if a {@link ParameterPassing} is built then the <code>inputref</code> is mutated to remove its text representation.
     * @return optionally, a {@link ParameterPassing}
     */
    public static Optional<ParameterPassing> build(AtomicReference<String> inputRef) {
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get());

        // Open parenthesis
        if (!Parenthesis.open(defensiveCopy)) {
            return Optional.empty();
        }

        // Browse for arguments
        List<Statement> arguments = new ArrayList<>();
        while (true) {

            // Attempt to read a Statement
            Optional<? extends Statement> arg = Statement.build(defensiveCopy);
            if (!arg.isPresent()) {
                break;
            }
            arguments.add(arg.get());

            // Attempt to have a delimiter between statements?
            if (delimiter(defensiveCopy)) {
                continue; // Yay! another one!
            } else {
                break; // Hmm it's over
            }
        }

        // Attempt close parenthesis
        if (!Parenthesis.close(defensiveCopy)) {
            return Optional.empty();
        }

        // A ParameterPassing Object can be constructed, commit changes to the input and return the object
        inputRef.set(defensiveCopy.get());
        return Optional.of(new ParameterPassing(arguments));
    }

    private static boolean delimiter(AtomicReference<String> defensiveCopy) {
        Matcher separator = separatorPattern.matcher(defensiveCopy.get());
        if (!separator.lookingAt()) {
            return false;
        }
        defensiveCopy.set(defensiveCopy.get().substring(separator.end()));
        Whitespace.skipWhitespaceAndComments(defensiveCopy);
        return true;
    }

    public List<String> show(String prefix) {
        List<String> result = new ArrayList<>();
        if (parameters.size() == 0) {
            // No parameters
            return new ArrayList<>();
        } else if (parameters.size() == 1) {
            Statement arg = parameters.get(0);
            final List<String> showArg = arg.show("");
            if (showArg.size() == 1) {
                // Single 1-line parameter
                return Arrays.asList(prefix + showArg.get(0));
            } else {
                // Single parameter is long, needs multiple lines
                for (int j = 0; j < showArg.size(); j++) {
                    result.add(prefix + showArg.get(j));
                }
            }
        } else {
            // Multiple parameters
            for (int i = 0; i < parameters.size(); i++) {
                Statement arg = parameters.get(i);
                final List<String> showArg = arg.show("");
                for (int j = 0; j < showArg.size(); j++) {
                    result.add(prefix + showArg.get(j) + (i < parameters.size() - 1 && j == showArg.size() - 1 ? "," : "")); // separator
                }
            }
        }
        return result;
    }
}
