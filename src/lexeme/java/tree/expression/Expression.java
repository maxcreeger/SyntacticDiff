package lexeme.java.tree.expression;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaSyntax;
import lexeme.java.tree.JavaSyntaxVisitor;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import lexeme.java.tree.expression.statement.Return;
import lexeme.java.tree.expression.statement.Statement;
import tokenizer.CodeLocator.CodeBranch;

/**
 * Represents a Java Expression<br>
 * Expressions can be a method invocation, a variable declaration... but not a class or method definition.<br>
 * They are usually found in method body or field initialization.
 */
public abstract class Expression implements JavaSyntax {

    private static final Pattern endOfExpressionPattern = Pattern.compile(";");

    /**
     * Attempts to build an expression.
     * @param input the input text (will be mutated if object is built)
     * @return optionally, an Expression
     */
    public static Optional<? extends Expression> build(CodeBranch input) {
        if (findEndOfExpression(input)) {
            return Optional.of(new EmptyExpression());
        }

        // Try to find a 'return' statement
        Optional<Return> returnStatement = Return.build(input);
        if (returnStatement.isPresent()) {
            return returnStatement;
        }

        // Try to find a 'if' construct
        Optional<IfBlock> ifBlock = IfBlock.build(input);
        if (ifBlock.isPresent()) {
            return ifBlock;
        }

        // Try to find a 'try' construct
        Optional<TryCatchFinallyBlock> tryBlock = TryCatchFinallyBlock.build(input);
        if (tryBlock.isPresent()) {
            return tryBlock;
        }

        // Try to find a 'for' construct
        Optional<ForBlock> forBlock = ForBlock.build(input);
        if (forBlock.isPresent()) {
            return forBlock;
        }

        // Try to find a 'while' construct
        Optional<WhileBlock> whileBlock = WhileBlock.build(input);
        if (whileBlock.isPresent()) {
            return whileBlock;
        }

        // Try to find a 'do..while' construct
        Optional<DoWhileBlock> doWhileBlock = DoWhileBlock.build(input);
        if (doWhileBlock.isPresent()) {
            return doWhileBlock;
        }

        CodeBranch defensiveCopy = input.fork();

        // Try to find a variable declaration
        Optional<? extends Expression> optional = VariableDeclaration.build(defensiveCopy);
        if (optional.isPresent()) {
            if (findEndOfExpression(defensiveCopy)) {
                // Commit
                defensiveCopy.commit();
                return optional;
            } else {
                // Revert
                defensiveCopy = input.fork();
            }
        }

        // Try to find a simple statement
        optional = Statement.build(defensiveCopy);
        if (optional.isPresent()) {
            if (findEndOfExpression(defensiveCopy)) {
                // Commit
                defensiveCopy.commit();
                return optional;
            } else {
                // Revert
                defensiveCopy = input.fork();
            }
        }

        return Optional.empty();
    }

    /**
     * Finds if the expression is over (at the first ";")
     * @param input the input text (is mutated if expression is over)
     * @return true if expression is over
     */
    public static boolean findEndOfExpression(CodeBranch input) {
        // Expect end of declaration
        Matcher endPattern = endOfExpressionPattern.matcher(input.getRest());
        if (!endPattern.lookingAt()) {
            return false;
        }
        input.advance(endPattern.end());
        JavaWhitespace.skipWhitespaceAndComments(input);
        return true;
    }

    // display

    @Override
    public String toString() {
        return String.join("\n", show(""));
    }

    @Override
    public <T> T acceptSyntaxVisitor(JavaSyntaxVisitor<T> visitor) {
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
