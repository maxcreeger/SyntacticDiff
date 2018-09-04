package lexeme.java.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lexer.Structure;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * An import statement.
 */
@Getter
public class ImportStatement implements Showable, Structure<JavaGrammar> {

    private static final Pattern importStatementPattern = Pattern.compile("^import\\s+(\\w+\\.)*(\\w+)+;");

    private final List<String> prefixPackages;
    private final List<String> suffixClasses;
    private final boolean endsWithStar;
    private final CodeLocation location;

    /**
     * Create an import statement using a {@link List} of consecutive package names, like "import package1.package2.package3";
     * 
     * @param packages the list of packages
     */
    public ImportStatement(List<String> packages, CodeLocation location) {
        this.prefixPackages = new ArrayList<>(packages);
        this.suffixClasses = new ArrayList<>();
        this.endsWithStar = false;
        this.location = location;
    }

    /**
     * Create an import statement using a {@link List} of consecutive package names, and end with a star, like "import package1.package2.package3.*";
     * 
     * @param packages the list of packages
     * @param endsWithStar a boolean. If false, does not end with a star.
     */
    public ImportStatement(List<String> packages, boolean endsWithStar, CodeLocation location) {
        this.prefixPackages = new ArrayList<>(packages);
        this.suffixClasses = new ArrayList<>();
        this.endsWithStar = endsWithStar;
        this.location = location;
    }

    /**
     * Create an import statement using a {@link List} of consecutive package names, then class names, like "import package1.package2.Class1.Class2";
     * 
     * @param packages the list of packages
     * @param classes the list of classes
     */
    public ImportStatement(List<String> packages, List<String> classes, CodeLocation location) {
        this.prefixPackages = new ArrayList<>(packages);
        this.suffixClasses = new ArrayList<>(classes);
        this.endsWithStar = false;
        this.location = location;
    }

    /**
     * Create an import statement using a {@link List} of consecutive package names, then class names, and end with a star, like "import package1.package2.Class1.Class2.*";
     * 
     * @param packages the list of packages
     * @param classes the list of classes
     * @param endsWithStar a boolean. If false, does not end with a star.
     */
    public ImportStatement(List<String> packages, List<String> classes, boolean endsWithStar, CodeLocation location) {
        this.prefixPackages = new ArrayList<>(packages);
        this.suffixClasses = new ArrayList<>(classes);
        this.endsWithStar = endsWithStar;
        this.location = location;
    }

    /**
     * Attempts to build an import statement
     * 
     * @param input the input text (is mutated if object is built)
     * @return optionally, an {@link ImportStatement}
     */
    public static Optional<ImportStatement> build(CodeBranch input) {
        CodeBranch fork = input.fork();
        Matcher matcher = importStatementPattern.matcher(fork.getRest());
        if (matcher.lookingAt()) {
            String importName = matcher.group(0);
            // System.out.println("Import statement found: " + importName);

            // Commit
            fork.advance(matcher.end());
            JavaWhitespace.skipWhitespaceAndComments(fork);
            return Optional.of(new ImportStatement(Arrays.asList(importName.split("\\.")), fork.commit()));
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
        final String packages = String.join(".", prefixPackages);
        final String classes = suffixClasses.isEmpty() ? "" : "." + String.join(".", suffixClasses);
        final String finalStar = endsWithStar ? ".*" : "";
        return Arrays.asList(prefix + packages + classes + finalStar);
    }

    /**
     * Returns the java-correct import statement
     * @return the java-correct import statement
     */
    public String getImportStatement() {
        String packages = String.join(".", prefixPackages);
        String classNames = String.join(".", suffixClasses);
        return (packages.isEmpty() ? "" : packages + ".") + classNames;
    }
}
