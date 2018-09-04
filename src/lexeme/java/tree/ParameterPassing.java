package lexeme.java.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lexeme.java.intervals.Parenthesis;
import lexeme.java.tree.expression.statement.Statement;
import lexer.Structure;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents parameters that are passed during a method invocation.
 */
@Getter
@AllArgsConstructor
public class ParameterPassing implements Showable, Structure<JavaGrammar> {

    private static final Pattern separatorPattern = Pattern.compile(",");

    private final List<Statement> parameters;
    private final CodeLocation location;

    /**
     * Attempts to build a {@link List} of Statements that are passed as method parameters inside parenthesis (objA, objB).
     * @param inputRef a mutable String, if a {@link ParameterPassing} is built then the <code>inputref</code> is mutated to remove its text representation.
     * @return optionally, a {@link ParameterPassing}
     */
    public static Optional<ParameterPassing> build(CodeBranch inputRef) {
        CodeBranch defensiveCopy = inputRef.fork();

        // Open parenthesis
        if (!Parenthesis.open(defensiveCopy)) {
            return Optional.empty();
        }

        // Browse for arguments (could be none)
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
        return Optional.of(new ParameterPassing(arguments, defensiveCopy.commit()));
    }

    private static boolean delimiter(CodeBranch code) {
        Matcher separator = separatorPattern.matcher(code.getRest());
        if (!separator.lookingAt()) {
            return false;
        }
        code.advance(separator.end());
        JavaWhitespace.skipWhitespaceAndComments(code);
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
