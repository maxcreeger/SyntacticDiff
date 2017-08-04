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
import parser.syntaxtree.ParameterPassing;
import parser.syntaxtree.Whitespace;

/**
 * Method invocation like obj.doSomething().
 */
@Getter
@AllArgsConstructor
public class MethodInvocation extends Statement {

    private static final Pattern methodNamePattern = Pattern.compile("\\w+");

    private final String methodName;
    private final ParameterPassing arguments;

    /**
     * Attempts to build a method invocation.
     * @param inputRef the input text (will be mutated if object is built)
     * @return optionally, a {@link MethodInvocation}
     */
    public static Optional<MethodInvocation> build(AtomicReference<String> inputRef) {
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get());

        // Method name
        Matcher methodName = methodNamePattern.matcher(defensiveCopy.get());
        if (!methodName.lookingAt()) {
            return Optional.empty();
        }
        defensiveCopy.set(defensiveCopy.get().substring(methodName.end()));
        Whitespace.skipWhitespaceAndComments(defensiveCopy);

        // Arguments in parenthesis
        Optional<ParameterPassing> arguments = ParameterPassing.build(defensiveCopy);
        if (!arguments.isPresent()) {
            return Optional.empty();
        }
        inputRef.set(defensiveCopy.get());
        return Optional.of(new MethodInvocation(methodName.group(0), arguments.get()));
    }

    @Override
    public boolean isAssignable() {
        return false;
    }

    // display

    @Override
    public List<String> show(String prefix) {
        final List<String> showParameters = arguments.show(prefix);
        if (showParameters.size() == 0) {
            return Arrays.asList(prefix + methodName + "( )");
        } else if (showParameters.size() == 1) {
            return Arrays.asList(prefix + methodName + "( " + showParameters.get(0) + " )");
        } else {
            List<String> result = new ArrayList<>();
            result.add(prefix + methodName + "( ");
            for (int i = 0; i < showParameters.size(); i++) {
                result.add(prefix + "c  " + showParameters.get(i) + (i == showParameters.size() - 1 ? " )" : ""));
            }
            return result;
        }
    }

    @Override
    public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
