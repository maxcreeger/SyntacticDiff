package lexeme.java.tree.expression.blocks.trycatchfinally;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lexeme.java.intervals.Curvy;
import lexeme.java.intervals.Parenthesis;
import lexeme.java.tree.ClassName;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.statement.VariableReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import settings.SyntacticSettings;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

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
    private final CodeLocation location;

    /**
     * Attempts to build a {@link CatchBlock}
     * @param input the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static List<CatchBlock> build(CodeBranch input) {
        List<CatchBlock> allCatchBlocks = new ArrayList<>();
        Optional<CatchBlock> oneCatch = buildOne(input);
        while (oneCatch.isPresent()) {
            allCatchBlocks.add(oneCatch.get());
            oneCatch = buildOne(input);
        }
        return allCatchBlocks;
    }

    private static Optional<CatchBlock> buildOne(CodeBranch input) {
        CodeBranch fork = input.fork();

        // Match 'catch' keyword
        Matcher catchMatcher = CATCH.matcher(fork.getRest());
        if (!catchMatcher.lookingAt()) {
            return Optional.empty();
        }
        fork.advance(catchMatcher.end());
        JavaWhitespace.skipWhitespaceAndComments(fork);

        // Begin catch exception declaration
        if (!Parenthesis.open(fork)) {
            return Optional.empty();
        }

        // Get each class name
        List<ClassName> exceptionTypes = new ArrayList<>();
        Optional<ClassName> exceptionType = ClassName.build(fork);
        while (exceptionType.isPresent()) {
            exceptionTypes.add(exceptionType.get());
            Matcher separatorMatcher = EXCEPTION_SEPARATOR.matcher(fork.getRest());
            if (!separatorMatcher.lookingAt()) {
                break; // No more separators
            }
            fork.advance(separatorMatcher.end());
            JavaWhitespace.skipWhitespaceAndComments(fork);
        }
        if (exceptionTypes.isEmpty()) {
            return Optional.empty(); // No exception class names found
        }

        // Get exception var name
        Optional<VariableReference> exceptionReference = VariableReference.build(fork);
        if (!exceptionReference.isPresent()) {
            return Optional.empty();
        }

        // End of exception declaration resources
        if (!Parenthesis.close(fork)) {
            return Optional.empty(); // Exception list not closed
        }

        // 'catch' Block - begin
        List<Expression> catchExpressions = new ArrayList<>();
        if (!Curvy.open(fork)) {
            return Optional.empty();
        }

        // 'catch' Block - content
        while (!Curvy.close(fork)) {
            Optional<? extends Expression> expr = Expression.build(fork);
            if (expr.isPresent()) {
                catchExpressions.add(expr.get());
            } else {
                return Optional.empty();
            }
        }

        // System.out.println(">Catch block detected");
        return Optional.of(new CatchBlock(exceptionTypes, exceptionReference.get(), catchExpressions, fork.commit()));
    }

    // Display

    public List<String> show(String prefix) {
        List<String> res = new ArrayList<>();

        // Exception types in header
        StringBuilder exceptionTypesList =
                new StringBuilder(prefix + SyntacticSettings.red() + SyntacticSettings.bold() + "catch" + SyntacticSettings.reset() + " (");
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
