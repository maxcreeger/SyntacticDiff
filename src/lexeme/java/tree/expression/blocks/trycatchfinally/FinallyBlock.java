package lexeme.java.tree.expression.blocks.trycatchfinally;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lexeme.java.intervals.Curvy;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a finally block, with a body.
 */
@Getter
@AllArgsConstructor
public final class FinallyBlock implements Showable {

    private static final Pattern FINALLY = Pattern.compile("finally");

    private final List<Expression> finallyExpressions;
    private final CodeLocation location;

    /**
     * Attempts to build a {@link FinallyBlock}
     * @param input the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static Optional<FinallyBlock> build(CodeBranch input) {
        CodeBranch fork = input.fork();

        // Match 'finally' keyword
        Matcher finallyMatcher = FINALLY.matcher(fork.getRest());
        if (!finallyMatcher.lookingAt()) {
            return Optional.empty();
        }
        fork.advance(finallyMatcher.end());
        JavaWhitespace.skipWhitespaceAndComments(fork);

        // 'finally' Block - begin
        List<Expression> finallyExpressions = new ArrayList<>();
        if (!Curvy.open(fork)) {
            return Optional.empty();
        }

        // 'finally' Block - content
        while (!Curvy.close(fork)) {
            Optional<? extends Expression> expr = Expression.build(fork);
            if (expr.isPresent()) {
                finallyExpressions.add(expr.get());
            } else {
                return Optional.empty();
            }
        }

        // System.out.println(">Finally block detected");
        return Optional.of(new FinallyBlock(finallyExpressions, fork.commit()));
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
