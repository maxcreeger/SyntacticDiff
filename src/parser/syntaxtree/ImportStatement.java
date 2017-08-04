package parser.syntaxtree;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An import statement.
 */
@AllArgsConstructor
@Getter
public class ImportStatement implements Showable {

    private static final Pattern importStatementPattern = Pattern.compile("^import\\s+(\\w+\\.)*(\\w+)+;");

    private final String importStatement;

    /**
     * Attempts to build an import statement
     * @param input the input text (is mutated if object is built)
     * @return optionally, an {@link ImportStatement}
     */
    public static Optional<ImportStatement> build(AtomicReference<String> input) {
        Matcher matcher = importStatementPattern.matcher(input.get());
        if (matcher.lookingAt()) {
            String importName = matcher.group(0);
            // System.out.println("Import statement found: " + importName);

            // Commit
            input.set(input.get().substring(matcher.end()));
            Whitespace.skipWhitespaceAndComments(input);
            return Optional.of(new ImportStatement(importName));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return String.join("\n", show(""));
    }

    @Override
    public List<String> show(String prefix) {
        return Arrays.asList(prefix + importStatement);
    }

}
