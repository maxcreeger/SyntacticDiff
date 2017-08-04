package parser.syntaxtree.expression.blocks.trycatchfinally;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.ClassName;
import parser.syntaxtree.Whitespace;
import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.statement.VariableReference;
import parser.tokens.Curvy;
import parser.tokens.Parenthesis;

/**
 * Represents a catch block, with Exception reference, and a body.
 */
@Getter
@AllArgsConstructor
public final class CatchBlock implements Showable {

    private static final Pattern CATCH = Pattern.compile("catch");
    private static final Pattern EXCEPTION_SEPARATOR = Pattern.compile("\\|");

    private final List<ClassName> exceptionTypes;
    private final VariableReference exceptionReference;
    private final List<Expression> catchExpressions;

    /**
     * Attempts to build a {@link CatchBlock}
     * @param input the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static List<CatchBlock> build(AtomicReference<String> input) {
        List<CatchBlock> allCatchBlocks = new ArrayList<>();
        Optional<CatchBlock> oneCatch = buildOne(input);
        while (oneCatch.isPresent()) {
            allCatchBlocks.add(oneCatch.get());
            oneCatch = buildOne(input);
        }
        return allCatchBlocks;
    }

    private static Optional<CatchBlock> buildOne(AtomicReference<String> input) {
        // Match 'catch' keyword
        Matcher catchMatcher = CATCH.matcher(input.get());
        if (!catchMatcher.lookingAt()) {
            return Optional.empty();
        }
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(input.get().substring(catchMatcher.end()));
        Whitespace.skipWhitespaceAndComments(defensiveCopy);

        // Begin catch exception declaration
        if (!Parenthesis.open(defensiveCopy)) {
            return Optional.empty();
        }

        // Get each class name
        List<ClassName> exceptionTypes = new ArrayList<>();
        Optional<ClassName> exceptionType = ClassName.build(defensiveCopy);
        while (exceptionType.isPresent()) {
            exceptionTypes.add(exceptionType.get());
            Matcher separatorMatcher = EXCEPTION_SEPARATOR.matcher(defensiveCopy.get());
            if (!separatorMatcher.lookingAt()) {
                break; // No more separators
            }
            defensiveCopy.set(defensiveCopy.get().substring(separatorMatcher.end()));
            Whitespace.skipWhitespaceAndComments(defensiveCopy);
        }
        if (exceptionTypes.isEmpty()) {
            return Optional.empty(); // No exception class names found
        }

        // Get exception var name
        Optional<VariableReference> exceptionReference = VariableReference.build(defensiveCopy);
        if (!exceptionReference.isPresent()) {
            return Optional.empty();
        }

        // End of exception declaration resources
        if (!Parenthesis.close(defensiveCopy)) {
            return Optional.empty(); // Exception list not closed
        }

        // 'catch' Block - begin
        List<Expression> catchExpressions = new ArrayList<>();
        if (!Curvy.open(defensiveCopy)) {
            return Optional.empty();
        }

        // 'catch' Block - content
        while (!Curvy.close(defensiveCopy)) {
            Optional<? extends Expression> expr = Expression.build(defensiveCopy);
            if (expr.isPresent()) {
                catchExpressions.add(expr.get());
            } else {
                return Optional.empty();
            }
        }

        // Commit
        input.set(defensiveCopy.get());
        // System.out.println(">Catch block detected");
        return Optional.of(new CatchBlock(exceptionTypes, exceptionReference.get(), catchExpressions));
    }

    // Display

    public List<String> show(String prefix) {
        List<String> res = new ArrayList<>();

        // Exception types in header
        StringBuilder exceptionTypesList = new StringBuilder(prefix + "catch (");
        Iterator<ClassName> iter = exceptionTypes.iterator();
        while (iter.hasNext()) {
            exceptionTypesList.append(iter.next().toString());
            if (iter.hasNext()) {
                exceptionTypesList.append(" | ");
            }
        }
        // Exception var name
        exceptionTypesList.append(" ").append(exceptionReference.toString()).append(") {");
        res.add(exceptionTypesList.toString());

        // Exception handling expressions
        for (Expression expression : catchExpressions) {
            res.addAll(expression.show(prefix + "h  "));
        }
        res.add(prefix + "} ");

        return res;
    }
}
