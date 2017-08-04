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
import parser.syntaxtree.ClassName;
import parser.syntaxtree.ParameterPassing;
import parser.syntaxtree.Whitespace;

/**
 * Represents a call to a constructor "new Object(arguments...)".
 */
@Getter
@AllArgsConstructor
public class NewInstance extends Statement {

    private static final Pattern newPattern = Pattern.compile("new\\s+");

    private final ClassName className;
    private final ParameterPassing constructorArguments;

    public static Optional<NewInstance> build(AtomicReference<String> input) {
        Matcher newMatcher = newPattern.matcher(input.get());
        if (!newMatcher.lookingAt()) {
            return Optional.empty();
        }

        // Reserved keyword 'new' has been found
        input.set(input.get().substring(newMatcher.end()));
        Whitespace.skipWhitespaceAndComments(input);

        Optional<ClassName> className = ClassName.build(input);
        if (!className.isPresent()) {
            throw new RuntimeException("Expecting Constructor after 'new' keyword");
        }

        Optional<ParameterPassing> params = ParameterPassing.build(input);
        return Optional.of(new NewInstance(className.get(), params.get()));
    }

    @Override
    public boolean isAssignable() {
        return false;
    }

    // display

    @Override
    public List<String> show(String prefix) {
        final List<String> showArguments = constructorArguments.show("");
        if (showArguments.size() == 0) {
            return Arrays.asList(prefix + "new " + className + "( )");
        } else if (showArguments.size() == 1) {
            return Arrays.asList(prefix + "new " + className + "( " + showArguments.get(0) + " )");
        } else {
            List<String> result = new ArrayList<>();
            result.add(prefix + "new " + className + "(");
            for (String line : showArguments) {
                result.add(prefix + "n  " + line);
            }
            return result;
        }
    }

    @Override
    public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
