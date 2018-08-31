package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a series of '.' statement acting on other statements like <code>new Object().toString().someField</code>. <br>
 * The dot may represent field accesses or method invocations, or Class name hierarchy (<code>Type.SubType.staticMethod()</code>)
 */
@Getter
@AllArgsConstructor
public class ChainedAccess extends Statement {

    private static final Pattern beginInvocation = Pattern.compile("\\.");

    private final List<Statement> statements;

    /**
     * Attempts to build a {@link ChainedAccess} from a mutable input String.
     * @param source an identified subject of the access
     * @param inputRef the input string. If a match is found, its text representation is removed from the input string
     * @return optionally, a {@link ChainedAccess}
     */
    public static Optional<ChainedAccess> build(Statement source, AtomicReference<String> inputRef) {
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get());
        List<Statement> statements = new ArrayList<>();
        statements.add(source);
        while (startChaining(defensiveCopy)) {  // Expect a '.'
            // Attempt to find method invocation
            Optional<MethodInvocation> optionalMethodInvocation = MethodInvocation.build(defensiveCopy);
            if (optionalMethodInvocation.isPresent()) {
                statements.add(optionalMethodInvocation.get());
                continue;
            }

            // Attempt to find field access
            Optional<VariableReference> optionalVarRef = VariableReference.build(defensiveCopy);
            if (optionalVarRef.isPresent()) {
                statements.add(optionalVarRef.get());
                continue;
            }
        }

        // Nothing found, end of chain
        if (statements.size() <= 1) {
            return Optional.empty(); // No chaining found after source
        } else {
            inputRef.set(defensiveCopy.get());
            return Optional.of(new ChainedAccess(statements));
        }
    }

    private static boolean startChaining(AtomicReference<String> input) {
        Matcher beginMatcher = beginInvocation.matcher(input.get());
        if (!beginMatcher.lookingAt()) {
            return false;
        } else {
            input.set(input.get().substring(beginMatcher.end()));
            JavaWhitespace.skipWhitespaceAndComments(input);
            return true;
        }
    }

    @Override
    public boolean isAssignable() {
        return statements.get(statements.size() - 1).isAssignable(); // Last statement would be the target of the assignment
    }

    // Display

    @Override
    public List<String> show(String prefix) {
        List<String> result = new ArrayList<>();

        // Source statement
        final Statement firstStatement = statements.get(0);
        final List<String> firstStatementShow = firstStatement.show("");
        final String firstStatementFirstLine = firstStatementShow.get(0);
        result.add(prefix + firstStatementFirstLine);
        for (int lineNum = 1; lineNum < firstStatementShow.size(); lineNum++) {
            result.add(prefix + ".  " + firstStatementShow.get(lineNum));
        }
        String alignment = new String(new char[firstStatementFirstLine.length()]).replace("\0", " ");

        // Subsequent chained calls
        for (int statementNum = 1; statementNum < statements.size(); statementNum++) {
            Statement statement = statements.get(statementNum);
            final List<String> statementShow = statement.show("");
            final String statementFirstLine = statementShow.get(0);
            result.add(prefix + alignment + "." + statementFirstLine);
            for (int lineNum = 1; lineNum < statementShow.size(); lineNum++) {
                result.add(prefix + alignment + statementShow.get(lineNum));
            }
        }
        return result;
    }

    @Override
    public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
