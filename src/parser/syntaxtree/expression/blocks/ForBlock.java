package parser.syntaxtree.expression.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.Whitespace;
import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.statement.Statement;
import parser.tokens.Curvy;
import parser.tokens.Parenthesis;

/**
 * Represents a for loop.<br>
 * Has an initialization {@link Expression}, an evaluation condition {@link Statement}, an iteration {@link Statement}, and a body of {@link Expression}s.
 */
@Getter
@AllArgsConstructor
public class ForBlock extends AbstractBlock {

    private static final Pattern forPattern = Pattern.compile("for");

    private final Expression initialisation;
    private final Statement evaluation;
    private final Expression iteration;
    private final List<Expression> body;

    /**
     * Attempts to build a {@link ForBlock}
     * @param inputRef the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static Optional<ForBlock> build(AtomicReference<String> inputRef) {
        // Match 'for' keyword
        Matcher forMatcher = forPattern.matcher(inputRef.get());
        if (!forMatcher.lookingAt()) {
            return Optional.empty();
        }
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get().substring(forMatcher.end()));
        Whitespace.skipWhitespaceAndComments(defensiveCopy);

        // Begin condition
        if (!Parenthesis.open(defensiveCopy)) {
            return Optional.empty();
        }

        // Initialization expression
        Optional<? extends Statement> initStatement = Statement.build(defensiveCopy);
        if (!initStatement.isPresent() || !findEndOfExpression(defensiveCopy)) {
            return Optional.empty();
        }

        // Evaluation expression
        Optional<? extends Statement> evalStatement = Statement.build(defensiveCopy);
        if (!evalStatement.isPresent() || !findEndOfExpression(defensiveCopy)) {
            return Optional.empty();
        }

        // Iteration expression
        Optional<? extends Statement> iterStatement = Statement.build(defensiveCopy);
        if (!initStatement.isPresent() || !findEndOfExpression(defensiveCopy)) {
            return Optional.empty();
        }

        // End of loop definition, start of body
        if (!Parenthesis.close(defensiveCopy)) {
            return Optional.empty();
        }

        // 'for' instructions
        List<Expression> forExpressions = new ArrayList<>();
        if (Curvy.open(defensiveCopy)) {
            // 'for' Block
            while (!Curvy.close(defensiveCopy)) {
                Optional<? extends Expression> expr = Expression.build(defensiveCopy);
                if (expr.isPresent()) {
                    forExpressions.add(expr.get());
                } else {
                    return Optional.empty();
                }
            }
        } else {
            // Single 'for' expression
            Optional<? extends Expression> expr = Expression.build(defensiveCopy);
            if (expr.isPresent()) {
                forExpressions.add(expr.get());
            }
        }

        // Commit
        inputRef.set(defensiveCopy.get());
        System.out.println("FOR loop detected");
        return Optional.of(new ForBlock(initStatement.get(), evalStatement.get(), iterStatement.get(), forExpressions));

    }

    // Visit

    @Override
    public <T> T acceptBlockVisitor(BlockVisitor<T> visitor) {
        return visitor.visit(this);
    }

    // Display

    @Override
    public List<String> show(String prefix) {
        List<String> result = new ArrayList<>();
        result.add(prefix + "for (");
        result.addAll(initialisation.show(prefix + "init> "));
        result.addAll(evaluation.show(prefix + "eval> "));
        result.addAll(iteration.show(prefix + "iter> "));
        String bodyPrefix = prefix + "f  ";
        for (Expression expression : body) {
            result.addAll(expression.show(bodyPrefix));
        }
        result.add(prefix + "}");
        return result;
    }

}
