package lexeme.java.tree.expression.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import diff.similarity.SimpleSimilarity.ShowableString;
import lexeme.java.intervals.Curvy;
import lexeme.java.intervals.Parenthesis;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.statement.Statement;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a do...while(expression) block.
 */
@Getter
public class DoWhileBlock extends AbstractBlock {

	private static final Pattern doPattern = Pattern.compile("do");
	private static final Pattern whilePattern = Pattern.compile("while");

	private final ShowableString doKeyword;
	private final List<Expression> body;
	private final ShowableString whileKeyword;
	private final Statement evaluation;

	public DoWhileBlock(ShowableString doKeyword, List<Expression> body, ShowableString whileKeyword, Statement evaluation, CodeLocation location) {
		super(location);
		this.doKeyword = doKeyword;
		this.body = body;
		this.whileKeyword = whileKeyword;
		this.evaluation = evaluation;
	}

	/**
	 * Attempts to build a {@link DoWhileBlock}
	 * 
	 * @param inputRef
	 *            the input text (is modified if the block is built)
	 * @return optionally, the block
	 */
	public static Optional<DoWhileBlock> build(CodeBranch inputRef) {
		CodeBranch fork = inputRef.fork();

		// Match 'do' keyword
		Optional<ShowableString> doKeyword = ShowableString.fromPattern(fork, doPattern);
		if (!doKeyword.isPresent()) {
			return Optional.empty();
		}

		// 'do' instructions
		List<Expression> doExpressions = new ArrayList<>();
		if (Curvy.open(fork)) {
			// 'do' Block
			while (!Curvy.close(fork)) {
				Optional<? extends Expression> expr = Expression.build(fork);
				if (expr.isPresent()) {
					doExpressions.add(expr.get());
				} else {
					return Optional.empty();
				}
			}
		} else {
			// Single 'do' expression
			Optional<? extends Expression> expr = Expression.build(fork);
			expr.ifPresent(doExpressions::add);
		}

		// Expect 'while' keyword
		Optional<ShowableString> whileKeyword = ShowableString.fromPattern(fork, whilePattern);
		if (!whileKeyword.isPresent()) {
			return Optional.empty();
		}

		// Begin condition
		if (!Parenthesis.open(fork)) {
			return Optional.empty();
		}

		// Evaluation expression
		Optional<? extends Statement> evalStatement = Statement.build(fork);
		if (!evalStatement.isPresent() || !findEndOfExpression(fork).isPresent()) {
			return Optional.empty();
		}

		// End of loop condition
		if (!Parenthesis.close(fork)) {
			return Optional.empty();
		}

		// System.out.println("Do...while loop detected");
		return Optional.of(new DoWhileBlock(doKeyword.get(), doExpressions, whileKeyword.get(), evalStatement.get(), fork.commit()));

	}

	@Override
	public <T> T acceptBlockVisitor(BlockVisitor<T> visitor) {
		return visitor.visit(this);
	}

	// Display
	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> result = new ArrayList<>();
		result.add(prefix + "do {");
		String doPrefix = prefix + "d  ";
		for (Expression expression : body) {
			result.addAll(expression.fullBreakdown(doPrefix));
		}
		result.add(prefix + "} while (");
		result.addAll(evaluation.fullBreakdown(doPrefix));
		result.add(prefix + ")");
		return result;
	}

}
