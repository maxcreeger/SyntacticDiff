package lexeme.java.tree.expression.blocks.trycatchfinally;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import diff.similarity.SimpleSimilarity.ShowableString;
import lexeme.java.intervals.Curvy;
import lexeme.java.tree.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a finally block, with a body.
 */
@Getter
@AllArgsConstructor
public final class FinallyBlock implements Showable {

	private static final Pattern FINALLY_PATTERN = Pattern.compile("finally");
	private final ShowableString finallyKeyword;
	private final List<Expression> finallyExpressions;
	private final CodeLocation location;

	/**
	 * Attempts to build a {@link FinallyBlock}
	 * 
	 * @param input
	 *            the input text (is modified if the block is built)
	 * @return optionally, the block
	 */
	public static Optional<FinallyBlock> build(CodeBranch input) {
		CodeBranch fork = input.fork();

		// Match 'finally' keyword
		Optional<ShowableString> finallyKeyword = ShowableString.fromPattern(input, FINALLY_PATTERN);
		if (!finallyKeyword.isPresent()) {
			return Optional.empty();
		}

		// 'finally' Block - begin
		List<Expression> finallyExpressions = new ArrayList<>();
		if (!Curvy.open(fork)) {
			return Optional.empty();
		}

		// 'finally' Block - content
		while (!Curvy.close(fork)) {
			Optional<? extends Expression> expr = Expression.build(fork);
			if (expr.isPresent()) {
				finallyExpressions.add(expr.get());
			} else {
				return Optional.empty();
			}
		}

		// System.out.println(">Finally block detected");
		return Optional.of(new FinallyBlock(finallyKeyword.get(), finallyExpressions, fork.commit()));
	}

	// Display

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> total = new ArrayList<>();
		total.add(prefix + "finally {");

		// Exception handling expressions
		for (Expression expression : finallyExpressions) {
			total.addAll(expression.fullBreakdown(prefix + "n  "));
		}
		total.add(prefix + "} ");
		return total;
	}
}
