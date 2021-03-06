package lexeme.java.tree.expression.statement.operators.unary.prefix;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.operators.OperatorVisitor;
import lexeme.java.tree.expression.statement.operators.unary.UnaryOperatorVisitor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Pre-increment operator like <code>++a</code>. Increments, then reads the
 * value
 */
@Getter
public class PreIncrementOperator extends PrefixUnaryOperator {

	/**
	 * builds an {@link PreIncrementOperator} on a {@link Statement}.
	 * 
	 * @param rightHandSide
	 *            the statement
	 */
	public PreIncrementOperator(Statement rightHandSide, CodeLocation location) {
		super(rightHandSide, location);
	}

	private static final Pattern operatorPattern = Pattern.compile("\\+\\+");

	public static Optional<PreIncrementOperator> build(CodeBranch input) {
		CodeBranch fork = input.fork();
		if (!find(fork)) {
			return Optional.empty();
		}

		Optional<? extends Statement> rhs = Statement.build(fork);
		if (!rhs.isPresent() || !rhs.get().isAssignable()) {
			return Optional.empty();
		}
		return Optional.of(new PreIncrementOperator(rhs.get(), fork.commit()));

	}

	static boolean find(CodeBranch input) {
		Matcher nameMatcher = operatorPattern.matcher(input.getRest());
		if (!nameMatcher.lookingAt()) {
			return false;
		}
		input.advance(nameMatcher.end());
		JavaWhitespace.skipWhitespaceAndComments(input);
		return true;
	}

	@Override
	public <T> T acceptOperatorVisitor(OperatorVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <T> T acceptUnaryOperatorVisitor(UnaryOperatorVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String getOperatorSymbol() {
		return "++";
	}

}
