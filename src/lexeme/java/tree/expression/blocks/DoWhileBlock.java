package lexeme.java.tree.expression.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tokens.Curvy;
import lexeme.java.tokens.Parenthesis;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.statement.Statement;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a do...while(expression) block.
 */
@Getter
@AllArgsConstructor
public class DoWhileBlock extends AbstractBlock {

    private static final Pattern doPattern = Pattern.compile("do");
    private static final Pattern whilePattern = Pattern.compile("while");

    private final List<Expression> body;
    private final Statement evaluation;

    /**
     * Attempts to build a {@link DoWhileBlock}
     * @param inputRef the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static Optional<DoWhileBlock> build(AtomicReference<String> inputRef) {
        // Match 'do' keyword
        Matcher doMatcher = doPattern.matcher(inputRef.get());
        if (!doMatcher.lookingAt()) {
            return Optional.empty();
        }
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get().substring(doMatcher.end()));
        JavaWhitespace.skipWhitespaceAndComments(defensiveCopy);

        // 'do' instructions
        List<Expression> doExpressions = new ArrayList<>();
        if (Curvy.open(defensiveCopy)) {
            // 'do' Block
            while (!Curvy.close(defensiveCopy)) {
                Optional<? extends Expression> expr = Expression.build(defensiveCopy);
                if (expr.isPresent()) {
                    doExpressions.add(expr.get());
                } else {
                    return Optional.empty();
                }
            }
        } else {
            // Single 'do' expression
            Optional<? extends Expression> expr = Expression.build(defensiveCopy);
            if (expr.isPresent()) {
                doExpressions.add(expr.get());
            }
        }

        // Expect 'while' keyword
        Matcher whileMatcher = whilePattern.matcher(defensiveCopy.get());
        if (!whileMatcher.lookingAt()) {
            return Optional.empty();
        }
        defensiveCopy.set(defensiveCopy.get().substring(whileMatcher.end()));
        JavaWhitespace.skipWhitespaceAndComments(defensiveCopy);

        // Begin condition
        if (!Parenthesis.open(defensiveCopy)) {
            return Optional.empty();
        }

        // Evaluation expression
        Optional<? extends Statement> evalStatement = Statement.build(defensiveCopy);
        if (!evalStatement.isPresent() || !findEndOfExpression(defensiveCopy)) {
            return Optional.empty();
        }

        // End of loop condition
        if (!Parenthesis.close(defensiveCopy)) {
            return Optional.empty();
        }

        // Commit
        inputRef.set(defensiveCopy.get());
        System.out.println("Do...while loop detected");
        return Optional.of(new DoWhileBlock(doExpressions, evalStatement.get()));

    }

    @Override
    public <T> T acceptBlockVisitor(BlockVisitor<T> visitor) {
        return visitor.visit(this);
    }

    // Display
    @Override
    public List<String> show(String prefix) {
        List<String> result = new ArrayList<>();
        result.add(prefix + "do {");
        String doPrefix = prefix + "d  ";
        for (Expression expression : body) {
            result.addAll(expression.show(doPrefix));
        }
        result.add(prefix + "} while (");
        result.addAll(evaluation.show(doPrefix));
        result.add(prefix + ")");
        return result;
    }

}
