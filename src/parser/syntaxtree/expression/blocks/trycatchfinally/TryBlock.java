package parser.syntaxtree.expression.blocks.trycatchfinally;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.Whitespace;
import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.VariableDeclaration;
import parser.tokens.Curvy;
import parser.tokens.Parenthesis;

/**
 * Represents a try block. May contain resources, must have a body of expressions.
 */
@Getter
@AllArgsConstructor
public final class TryBlock implements Showable {

    private static final Pattern TRY = Pattern.compile("try");
    private static final Pattern RESOURCE_SEPARATOR = Pattern.compile(";");

    private final List<VariableDeclaration> tryWithResources;
    private final List<Expression> tryExpressions;

    protected static Optional<TryBlock> build(AtomicReference<String> input) {
        // Match 'try' keyword
        Matcher tryMatcher = TRY.matcher(input.get());
        if (!tryMatcher.lookingAt()) {
            return Optional.empty();
        }
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(input.get().substring(tryMatcher.end()));
        Whitespace.skipWhitespaceAndComments(defensiveCopy);
        List<VariableDeclaration> tryWithResources = new ArrayList<>();
        // Begin try-with-resources
        if (Parenthesis.open(defensiveCopy)) {
            // Resources declaration
            do {
                Optional<VariableDeclaration> varDecla = VariableDeclaration.build(defensiveCopy);
                if (varDecla.isPresent()) {
                    tryWithResources.add(varDecla.get());
                } else {
                    return Optional.empty(); // Failed to find a resource declaration
                }
            } while (findResourceSeparator(defensiveCopy));

            // End of try resources
            if (!Parenthesis.close(defensiveCopy)) {
                return Optional.empty(); // resources list not closed
            }
        }

        // 'try' Block - begin
        List<Expression> tryExpressions = new ArrayList<>();
        if (!Curvy.open(defensiveCopy)) {
            return Optional.empty();
        }
        // 'try' Block - content
        while (!Curvy.close(defensiveCopy)) {
            Optional<? extends Expression> expr = Expression.build(defensiveCopy);
            if (expr.isPresent()) {
                tryExpressions.add(expr.get());
            } else {
                return Optional.empty();
            }
        }

        // Commit
        input.set(defensiveCopy.get());
        // System.out.println(">Try block detected");
        return Optional.of(new TryBlock(tryWithResources, tryExpressions));
    }

    private static boolean findResourceSeparator(AtomicReference<String> input) {
        Matcher separatorMatcher = RESOURCE_SEPARATOR.matcher(input.get());
        if (!separatorMatcher.lookingAt()) {
            return false;
        } else {
            input.set(input.get().substring(separatorMatcher.end()));
            Whitespace.skipWhitespaceAndComments(input);
            return true;
        }
    }

    // Display

    @Override
    public List<String> show(String prefix) {
        List<String> total = new ArrayList<>();

        if (tryWithResources.isEmpty()) {
            total.add(prefix + "try {");
        } else {
            // Resources (optional)
            total.add(prefix + "try (");
            for (VariableDeclaration tryVar : tryWithResources) {
                total.addAll(tryVar.show(prefix + "r  "));
            }
            total.add(prefix + ") {");
        }

        // Body
        for (Expression expression : tryExpressions) {
            total.addAll(expression.show(prefix + "y  "));
        }

        // Close off
        total.add(prefix + "}");
        return total;
    }
}
