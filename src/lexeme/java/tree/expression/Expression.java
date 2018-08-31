package lexeme.java.tree.expression;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.Syntax;
import lexeme.java.tree.SyntaxVisitor;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import lexeme.java.tree.expression.statement.Return;
import lexeme.java.tree.expression.statement.Statement;

/**
 * Represents a Java Expression<br>
 * Expressions can be a method invocation, a variable declaration... but not a class or method definition.<br>
 * They are usually found in method body or field initialization.
 */
public abstract class Expression implements Syntax {

    private static final Pattern endOfExpressionPattern = Pattern.compile(";");

    /**
     * Attempts to build an expression.
     * @param input the input text (will be mutated if object is built)
     * @return optionally, an Expression
     */
    public static Optional<? extends Expression> build(AtomicReference<String> input) {
        if (findEndOfExpression(input)) {
            return Optional.of(new EmptyExpression());
        }
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(input.get());

        // Try to find a 'return' statement
        Optional<Return> returnStatement = Return.build(input);
        if (returnStatement.isPresent()) {
            return returnStatement;
        }

        // Try to find a 'if' construct
        Optional<IfBlock> ifBlock = IfBlock.build(defensiveCopy);
        if (ifBlock.isPresent()) {
            // commit
            input.set(defensiveCopy.get());
            return ifBlock;
        }

        // Try to find a 'try' construct
        Optional<TryCatchFinallyBlock> tryBlock = TryCatchFinallyBlock.build(defensiveCopy);
        if (tryBlock.isPresent()) {
            // commit
            input.set(defensiveCopy.get());
            return tryBlock;
        }

        // Try to find a 'for' construct
        Optional<ForBlock> forBlock = ForBlock.build(defensiveCopy);
        if (forBlock.isPresent()) {
            // commit
            input.set(defensiveCopy.get());
            return forBlock;
        }

        // Try to find a 'while' construct
        Optional<WhileBlock> whileBlock = WhileBlock.build(defensiveCopy);
        if (whileBlock.isPresent()) {
            // commit
            input.set(defensiveCopy.get());
            return whileBlock;
        }

        // Try to find a 'do..while' construct
        Optional<DoWhileBlock> doWhileBlock = DoWhileBlock.build(defensiveCopy);
        if (doWhileBlock.isPresent()) {
            // commit
            input.set(defensiveCopy.get());
            return doWhileBlock;
        }

        // Try to find a variable declaration
        Optional<? extends Expression> optional = VariableDeclaration.build(defensiveCopy);
        if (optional.isPresent()) {
            if (findEndOfExpression(defensiveCopy)) {
                // Commit
                input.set(defensiveCopy.get());
                return optional;
            } else {
                // Revert
                defensiveCopy = new AtomicReference<String>(input.get());
            }
        }

        // Try to find a simple statement
        optional = Statement.build(defensiveCopy);
        if (optional.isPresent()) {
            if (findEndOfExpression(defensiveCopy)) {
                // Commit
                input.set(defensiveCopy.get());
                return optional;
            } else {
                // Revert
                defensiveCopy = new AtomicReference<String>(input.get());
            }
        }

        return Optional.empty();
    }

    /**
     * Finds if the expression is over (at the first ";")
     * @param input the input text (is mutated if expression is over)
     * @return true if expression is over
     */
    public static boolean findEndOfExpression(AtomicReference<String> input) {
        // Expect end of declaration
        Matcher endPattern = endOfExpressionPattern.matcher(input.get());
        if (!endPattern.lookingAt()) {
            return false;
        }
        input.set(input.get().substring(endPattern.end()));
        JavaWhitespace.skipWhitespaceAndComments(input);
        return true;
    }

    // display

    @Override
    public String toString() {
        return String.join("\n", show(""));
    }

    @Override
    public <T> T acceptSyntaxVisitor(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Accept an {@link ExpressionVisitor}.
     * @param <T> the type of returned object
     * @param visitor the visitor
     * @return the returned object
     */
    public abstract <T> T acceptExpressionVisitor(ExpressionVisitor<T> visitor);
}
