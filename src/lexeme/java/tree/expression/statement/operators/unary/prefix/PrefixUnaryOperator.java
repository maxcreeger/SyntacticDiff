package lexeme.java.tree.expression.statement.operators.unary.prefix;

import java.util.List;
import java.util.Optional;

import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.operators.unary.UnaryOperator;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a prefix unary operator like <code>++a</code> or
 * <code>!isEmpty()</code>.
 */
@Getter
public abstract class PrefixUnaryOperator extends UnaryOperator {

	public PrefixUnaryOperator(Statement targetedStatement, CodeLocation location) {
		super(targetedStatement, location);
	}

	/**
	 * Attempts to build a {@link PrefixUnaryOperator} from an input text.
	 * 
	 * @param input
	 *            the mutable input text (is modified if the object is built)
	 * @return optionally, the operator
	 */
	public static Optional<? extends PrefixUnaryOperator> buildUnaryPrefix(CodeBranch input) {
		Optional<? extends PrefixUnaryOperator> opt = NotOperator.build(input);
		if (opt.isPresent()) {
			return opt;
		}

		opt = PreIncrementOperator.build(input);
		if (opt.isPresent()) {
			return opt;
		}

		return Optional.empty();
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		final List<String> result = targetedStatement.fullBreakdown("");
		final String operatorSymbol = getOperatorSymbol();
		for (int i = 0; i < result.size(); i++) {
			if (i == 0) {
				result.set(i, prefix + operatorSymbol + result.get(i));
			} else {
				result.set(i, prefix + operatorSymbol.length() + result.get(i));
			}
		}
		return result;
	}
}
