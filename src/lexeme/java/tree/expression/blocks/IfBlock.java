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
 * Represents an if block, with a conditional statement and a body. May have else expressions.
 */
@Getter
@AllArgsConstructor
public class IfBlock extends AbstractBlock {

    private static final Pattern ifPattern = Pattern.compile("if");
    private static final Pattern elsePattern = Pattern.compile("else");

    private final Statement condition;
    private final List<Expression> thenExpressions;
    private final List<Expression> elseExpressions;

    /**
     * Attempts to build a {@link IfBlock}
     * @param inputRef the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static Optional<IfBlock> build(AtomicReference<String> inputRef) {
        // Match 'if' keyword
        Matcher ifMatcher = ifPattern.matcher(inputRef.get());
        if (!ifMatcher.lookingAt()) {
            return Optional.empty();
        }
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get().substring(ifMatcher.end()));
        JavaWhitespace.skipWhitespaceAndComments(defensiveCopy);

        // Begin condition
        if (!Parenthesis.open(defensiveCopy)) {
            return Optional.empty();
        }
        // Conditional statement
        Optional<? extends Statement> optStatement = Statement.build(defensiveCopy);
        if (!optStatement.isPresent()) {
            return Optional.empty();
        }

        // end of conditional statement, start of body
        if (!Parenthesis.close(defensiveCopy)) {
            return Optional.empty();
        }

        // 'Then' instructions
        List<Expression> thenExpressions = new ArrayList<>();
        if (Curvy.open(defensiveCopy)) {
            // 'Then' Block
            while (!Curvy.close(defensiveCopy)) {
                Optional<? extends Expression> expr = Expression.build(defensiveCopy);
                if (expr.isPresent()) {
                    thenExpressions.add(expr.get());
                }
            }
        } else {
            // Single 'then' expression
            Optional<? extends Expression> expr = Expression.build(defensiveCopy);
            if (expr.isPresent()) {
                thenExpressions.add(expr.get());
            }
        }

        // Maybe else block?
        Matcher elseMatcher = elsePattern.matcher(defensiveCopy.get());
        List<Expression> elseExpressions = new ArrayList<>();
        if (elseMatcher.lookingAt()) {
            if (Curvy.open(defensiveCopy)) {
                // 'else' Block
                while (!Curvy.close(defensiveCopy)) {
                    Optional<? extends Expression> expr = Expression.build(defensiveCopy);
                    if (expr.isPresent()) {
                        elseExpressions.add(expr.get());
                    }
                }
            } else {
                // Single 'else' expression
                Optional<? extends Expression> expr = Expression.build(defensiveCopy);
                if (expr.isPresent()) {
                    elseExpressions.add(expr.get());
                }
            }
        }

        // Commit
        inputRef.set(defensiveCopy.get());
        // System.out.println("if[then][else] block detected");
        return Optional.of(new IfBlock(optStatement.get(), thenExpressions, elseExpressions));

    }

    @Override
    public <T> T acceptBlockVisitor(BlockVisitor<T> visitor) {
        return visitor.visit(this);
    }

    // Display

    @Override
    public List<String> show(String prefix) {
        List<String> result = new ArrayList<>();

        // IF
        List<String> conditionShow = condition.show("");
        if (conditionShow.size() == 1) {
            result.add(prefix + "if (" + conditionShow.get(0) + " ) {");
        } else {
            result.add(prefix + "if (");
            for (String line : conditionShow) {
                result.add(prefix + "?  " + line);
            }
            result.add(prefix + ") {");
        }

        // THEN
        String thenPrefix = prefix + "n  ";
        for (Expression expression : thenExpressions) {
            result.addAll(expression.show(thenPrefix));
        }

        // ELSE
        if (elseExpressions != null && !elseExpressions.isEmpty()) {
            result.add(prefix + "} else { ");
            String elsePrefix = prefix + "e  ";
            for (Expression expression : elseExpressions) {
                result.addAll(expression.show(elsePrefix));
            }
        }
        result.add(prefix + "}");
        return result;
    }

    @Override
    public List<Expression> getBody() {
        return thenExpressions;
    }

}
