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
 * Represents a while(boolean expression) { ... } block.
 */
@Getter
@AllArgsConstructor
public class WhileBlock extends AbstractBlock {

    private static final Pattern whilePattern = Pattern.compile("while");

    private final Statement evaluation;
    private final List<Expression> body;

    /**
     * Attempts to build a {@link WhileBlock}
     * @param inputRef the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static Optional<WhileBlock> build(AtomicReference<String> inputRef) {
        // Match 'while' keyword
        Matcher whileMatcher = whilePattern.matcher(inputRef.get());
        if (!whileMatcher.lookingAt()) {
            return Optional.empty();
        }
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get().substring(whileMatcher.end()));
        Whitespace.skipWhitespaceAndComments(defensiveCopy);

        // Begin condition
        if (!Parenthesis.open(defensiveCopy)) {
            return Optional.empty();
        }

        // Evaluation expression
        Optional<? extends Statement> evalStatement = Statement.build(defensiveCopy);
        if (!evalStatement.isPresent()) {
            return Optional.empty();
        }

        // End of loop definition, start of body
        if (!Parenthesis.close(defensiveCopy)) {
            return Optional.empty();
        }

        // 'while' instructions
        List<Expression> whileExpressions = new ArrayList<>();
        if (Curvy.open(defensiveCopy)) {
            // 'while' Block
            while (!Curvy.close(defensiveCopy)) {
                Optional<? extends Expression> expr = Expression.build(defensiveCopy);
                if (expr.isPresent()) {
                    whileExpressions.add(expr.get());
                } else {
                    return Optional.empty();
                }
            }
        } else {
            // Single 'while' expression
            Optional<? extends Expression> expr = Expression.build(defensiveCopy);
            if (expr.isPresent()) {
                whileExpressions.add(expr.get());
            }
        }

        // Commit
        inputRef.set(defensiveCopy.get());
        // System.out.println("while block detected");
        return Optional.of(new WhileBlock(evalStatement.get(), whileExpressions));

    }

    // Display

    @Override
    public List<String> show(String prefix) {
        List<String> result = new ArrayList<>();
        final List<String> showEvaluation = evaluation.show("");
        if (showEvaluation.size() == 1) {
            result.add(prefix + "while (" + showEvaluation.get(0) + " ) {");
        } else {
            result.add(prefix + "while (");
            for (String line : showEvaluation) {
                result.add(prefix + "?  " + line);
            }
            result.add(prefix + " ) {");
        }
        for (Expression expression : body) {
            result.addAll(expression.show(prefix + "w  "));
        }
        result.add(prefix + "}");
        return result;
    }

    @Override
    public <T> T acceptBlockVisitor(BlockVisitor<T> visitor) {
        return visitor.visit(this);
    }


}
