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
import parser.tokens.Curvy;

/**
 * Represents a finally block, with a body.
 */
@Getter
@AllArgsConstructor
public final class FinallyBlock implements Showable {

    private static final Pattern FINALLY = Pattern.compile("finally");

    private final List<Expression> finallyExpressions;

    /**
     * Attempts to build a {@link FinallyBlock}
     * @param input the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static Optional<FinallyBlock> build(AtomicReference<String> input) {
        // Match 'finally' keyword
        Matcher finallyMatcher = FINALLY.matcher(input.get());
        if (!finallyMatcher.lookingAt()) {
            return Optional.empty();
        }
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(input.get().substring(finallyMatcher.end()));
        Whitespace.skipWhitespaceAndComments(defensiveCopy);

        // 'finally' Block - begin
        List<Expression> finallyExpressions = new ArrayList<>();
        if (!Curvy.open(defensiveCopy)) {
            return Optional.empty();
        }

        // 'finally' Block - content
        while (!Curvy.close(defensiveCopy)) {
            Optional<? extends Expression> expr = Expression.build(defensiveCopy);
            if (expr.isPresent()) {
                finallyExpressions.add(expr.get());
            } else {
                return Optional.empty();
            }
        }

        // Commit
        input.set(defensiveCopy.get());
        // System.out.println(">Finally block detected");
        return Optional.of(new FinallyBlock(finallyExpressions));
    }

    // Display

    @Override
    public List<String> show(String prefix) {
        List<String> total = new ArrayList<>();
        total.add(prefix + "finally {");

        // Exception handling expressions
        for (Expression expression : finallyExpressions) {
            total.addAll(expression.show(prefix + "n  "));
        }
        total.add(prefix + "} ");
        return total;
    }
}
