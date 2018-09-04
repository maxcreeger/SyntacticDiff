package lexeme.java.tree.expression.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.intervals.Curvy;
import lexeme.java.intervals.Parenthesis;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.statement.Statement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

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
    private final CodeLocation location;

    /**
     * Attempts to build a {@link DoWhileBlock}
     * @param inputRef the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static Optional<DoWhileBlock> build(CodeBranch inputRef) {
        CodeBranch fork = inputRef.fork();

        // Match 'do' keyword
        Matcher doMatcher = doPattern.matcher(fork.getRest());
        if (!doMatcher.lookingAt()) {
            return Optional.empty();
        }
        fork.advance(doMatcher.end());
        JavaWhitespace.skipWhitespaceAndComments(fork);

        // 'do' instructions
        List<Expression> doExpressions = new ArrayList<>();
        if (Curvy.open(fork)) {
            // 'do' Block
            while (!Curvy.close(fork)) {
                Optional<? extends Expression> expr = Expression.build(fork);
                if (expr.isPresent()) {
                    doExpressions.add(expr.get());
                } else {
                    return Optional.empty();
                }
            }
        } else {
            // Single 'do' expression
            Optional<? extends Expression> expr = Expression.build(fork);
            if (expr.isPresent()) {
                doExpressions.add(expr.get());
            }
        }

        // Expect 'while' keyword
        Matcher whileMatcher = whilePattern.matcher(fork.getRest());
        if (!whileMatcher.lookingAt()) {
            return Optional.empty();
        }
        fork.advance(whileMatcher.end());
        JavaWhitespace.skipWhitespaceAndComments(fork);

        // Begin condition
        if (!Parenthesis.open(fork)) {
            return Optional.empty();
        }

        // Evaluation expression
        Optional<? extends Statement> evalStatement = Statement.build(fork);
        if (!evalStatement.isPresent() || !findEndOfExpression(fork)) {
            return Optional.empty();
        }

        // End of loop condition
        if (!Parenthesis.close(fork)) {
            return Optional.empty();
        }

        // System.out.println("Do...while loop detected");
        return Optional.of(new DoWhileBlock(doExpressions, evalStatement.get(), fork.commit()));

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
