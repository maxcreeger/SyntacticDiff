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
 * A package declaration (at the beginning of every Java class.
 */
@Getter
@AllArgsConstructor
public class PackageDeclaration implements Showable {

    private static final Pattern packageDeclarationPattern = Pattern.compile("^package\\s+(\\w+\\.)*(\\w+)+;");

    private final String packageDeclaration;

    /**
     * Attempts to build a {@link PackageDeclaration}
     * @param input the input text (is mutated if found)
     * @return optionally, a {@link PackageDeclaration}
     */
    public static Optional<PackageDeclaration> build(AtomicReference<String> input) {
        Matcher matcher = packageDeclarationPattern.matcher(input.get());
        if (matcher.lookingAt()) {
            String packageName = matcher.group(0);
            // System.out.println("Package declaration found: " + packageName);

            // Commit
            input.set(input.get().substring(matcher.end()));
            Whitespace.skipWhitespaceAndComments(input);
            return Optional.of(new PackageDeclaration(packageName));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return packageDeclaration;
    }

    @Override
    public List<String> show(String prefix) {
        return Arrays.asList(prefix + packageDeclaration);
    }
}
