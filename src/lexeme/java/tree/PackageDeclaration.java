package lexeme.java.tree;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lexer.Structure;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A package declaration (at the beginning of every Java class.
 */
@Getter
@AllArgsConstructor
public class PackageDeclaration implements Showable, Structure<JavaGrammar> {

    private static final Pattern packageDeclarationPattern = Pattern.compile("^package\\s+(\\w+\\.)*(\\w+)+;");

    private final List<String> packagePath; // has at least one element

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
            JavaWhitespace.skipWhitespaceAndComments(input);
            return Optional.of(new PackageDeclaration(Arrays.asList(packageName)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return String.join(".", packagePath);
    }

    @Override
    public List<String> show(String prefix) {
        return Arrays.asList(prefix + toString());
    }
}
