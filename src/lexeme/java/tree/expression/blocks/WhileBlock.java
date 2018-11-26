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
import settings.SyntacticSettings;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a while(boolean expression) { ... } block.
 */
@Getter
public class WhileBlock extends AbstractBlock {

	private static final Pattern whilePattern = Pattern.compile("while");

	private final ShowableString whileKeyword;
	private final Statement evaluation;
	private final List<Expression> body;

	public WhileBlock(ShowableString whileKeyword, Statement evaluation, List<Expression> body, CodeLocation location) {
		super(location);
		this.whileKeyword = whileKeyword;
		this.evaluation = evaluation;
		this.body = body;
	}

	/**
	 * Attempts to build a {@link WhileBlock}
	 * 
	 * @param inputRef
	 *            the input text (is modified if the block is built)
	 * @return optionally, the block
	 */
	public static Optional<WhileBlock> build(CodeBranch inputRef) {
		CodeBranch fork = inputRef.fork();

		// Match 'while' keyword
		Optional<ShowableString> keyword = ShowableString.fromPattern(fork, whilePattern);
		if (!keyword.isPresent()) {
			return Optional.empty();
		}

		// Begin condition
		if (!Parenthesis.open(fork)) {
			return Optional.empty();
		}

		// Evaluation expression
		Optional<? extends Statement> evalStatement = Statement.build(fork);
		if (!evalStatement.isPresent()) {
			return Optional.empty();
		}

		// End of loop definition, start of body
		if (!Parenthesis.close(fork)) {
			return Optional.empty();
		}

		// 'while' instructions
		List<Expression> whileExpressions = new ArrayList<>();
		if (Curvy.open(fork)) {
			// 'while' Block
			while (!Curvy.close(fork)) {
				Optional<? extends Expression> expr = Expression.build(fork);
				if (expr.isPresent()) {
					whileExpressions.add(expr.get());
				} else {
					return Optional.empty();
				}
			}
		} else {
			// Single 'while' expression
			Optional<? extends Expression> expr = Expression.build(fork);
			if (expr.isPresent()) {
				whileExpressions.add(expr.get());
			}
		}

		// System.out.println("while block detected");
		return Optional.of(new WhileBlock(keyword.get(), evalStatement.get(), whileExpressions, fork.commit()));

	}

	// Display

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> result = new ArrayList<>();
		final List<String> showEvaluation = evaluation.fullBreakdown("");
		if (showEvaluation.size() == 1) {
			result.add(
				prefix + SyntacticSettings.red() + SyntacticSettings.bold() + "while" + SyntacticSettings.reset() + " (" + showEvaluation.get(0) + " ) {");
		} else {
			result.add(prefix + SyntacticSettings.red() + SyntacticSettings.bold() + "while" + SyntacticSettings.reset() + " (");
			for (String line : showEvaluation) {
				result.add(prefix + "?  " + line);
			}
			result.add(prefix + " ) {");
		}
		for (Expression expression : body) {
			result.addAll(expression.fullBreakdown(prefix + "w  "));
		}
		result.add(prefix + "}");
		return result;
	}

	@Override
	public <T> T acceptBlockVisitor(BlockVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
