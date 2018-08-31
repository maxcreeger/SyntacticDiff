package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Reference to a variable in an expression like "obj" in <code>obj.toString()</code>
 */
@Getter
@AllArgsConstructor
public class VariableReference extends Statement {

    private static final Pattern variableNamePattern = Pattern.compile("\\w+");

    private final String variableName;

    /**
     * Attempts to build an reference to a variable.
     * @param inputRef the input text (will be mutated if object is built)
     * @return optionally, an variable reference
     */
    public static Optional<VariableReference> build(AtomicReference<String> inputRef) {
        Matcher variableNameMatcher = variableNamePattern.matcher(inputRef.get());
        if (!variableNameMatcher.lookingAt()) {
            return Optional.empty();
        } else {
            inputRef.set(inputRef.get().substring(variableNameMatcher.end()));
            JavaWhitespace.skipWhitespaceAndComments(inputRef);
            return Optional.of(new VariableReference(variableNameMatcher.group(0)));
        }
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public List<String> show(String prefix) {
        List<String> list = new ArrayList<>();
        list.add(variableName);
        return list;
    }

    @Override
    public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
