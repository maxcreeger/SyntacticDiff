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
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a Java Expression<br>
 * Expressions can be a method invocation, a variable declaration... but not a
 * class or method definition.<br>
 * They are usually found in method body or field initialization. The main
 * difference with Statement is that Expressions cannot be passed. They are a
 * structure, or a Statement with an ending Semicolon.
 */
@Getter
@AllArgsConstructor
public abstract class Expression implements JavaSyntax {

	private static final Pattern endOfExpressionPattern = Pattern.compile(";");
	private final CodeLocation location;

	/**
	 * Attempts to build an expression.
	 * 
	 * @param input
	 *            the input text (will be mutated if object is built)
	 * @return optionally, an Expression
	 */
	public static Optional<? extends Expression> build(CodeBranch input) {
		Optional<CodeLocation> endOfExpression = findEndOfExpression(input);
		if (endOfExpression.isPresent()) {
			return Optional.of(new EmptyExpression(endOfExpression.get()));
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

		// Try to find a 'return' statement
		Optional<Return> returnStatement = Return.build(defensiveCopy);
		if (returnStatement.isPresent()) {
			if (findEndOfExpression(defensiveCopy).isPresent()) {
				// Commit
				defensiveCopy.commit();
				return returnStatement;
			} else {
				// Revert
				defensiveCopy = input.fork();
			}
		}

		// Try to find a variable declaration
		Optional<? extends Expression> optional = VariableDeclaration.build(defensiveCopy);
		if (optional.isPresent()) {
			if (findEndOfExpression(defensiveCopy).isPresent()) {
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
			if (findEndOfExpression(defensiveCopy).isPresent()) {
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
	 * 
	 * @param input
	 *            the input text (is mutated if expression is over)
	 * @return true if expression is over
	 */
	public static Optional<CodeLocation> findEndOfExpression(CodeBranch input) {
		CodeBranch fork = input.fork();
		// Expect end of declaration
		Matcher endPattern = endOfExpressionPattern.matcher(fork.getRest());
		if (!endPattern.lookingAt()) {
			return Optional.empty();
		}
		fork.advance(endPattern.end());
		JavaWhitespace.skipWhitespaceAndComments(fork);
		return Optional.of(fork.commit());
	}

	// display

	@Override
	public String toString() {
		return String.join("\n", nativeFormat(""));
	}

	@Override
	public <T> T acceptSyntaxVisitor(JavaSyntaxVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Accept an {@link ExpressionVisitor}.
	 * 
	 * @param <T>
	 *            the type of returned object
	 * @param visitor
	 *            the visitor
	 * @return the returned object
	 */
	public abstract <T> T acceptExpressionVisitor(ExpressionVisitor<T> visitor);
}
