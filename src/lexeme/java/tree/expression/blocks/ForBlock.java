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
 * Represents a for loop.<br>
 * Has an initialization {@link Expression}, an evaluation condition
 * {@link Statement}, an iteration {@link Statement}, and a body of
 * {@link Expression}s.
 */
@Getter
public class ForBlock extends AbstractBlock {

	public ForBlock(ShowableString forKeyword, Expression initialisation, Statement evaluation, Expression iteration, List<Expression> body,
		CodeLocation location) {
		super(location);
		this.forKeyword = forKeyword;
		this.initialisation = initialisation;
		this.evaluation = evaluation;
		this.iteration = iteration;
		this.body = body;
	}

	private static final Pattern forPattern = Pattern.compile("for");

	private final ShowableString forKeyword;
	private final Expression initialisation;
	private final Statement evaluation;
	private final Expression iteration;
	private final List<Expression> body;

	/**
	 * Attempts to build a {@link ForBlock}
	 * 
	 * @param inputRef
	 *            the input text (is modified if the block is built)
	 * @return optionally, the block
	 */
	public static Optional<ForBlock> build(CodeBranch inputRef) {
		CodeBranch fork = inputRef.fork();

		// Match 'for' keyword
		Optional<ShowableString> forKeyword = ShowableString.fromPattern(fork, forPattern);
		if (!forKeyword.isPresent()) {
			return Optional.empty();
		}

		// Begin condition
		if (!Parenthesis.open(fork)) {
			return Optional.empty();
		}

		// Initialization expression
		Optional<? extends Statement> initStatement = Statement.build(fork);
		if (!initStatement.isPresent() || !findEndOfExpression(fork).isPresent()) {
			return Optional.empty();
		}

		// Evaluation expression
		Optional<? extends Statement> evalStatement = Statement.build(fork);
		if (!evalStatement.isPresent() || !findEndOfExpression(fork).isPresent()) {
			return Optional.empty();
		}

		// Iteration expression
		Optional<? extends Statement> iterStatement = Statement.build(fork);
		if (!initStatement.isPresent() || !findEndOfExpression(fork).isPresent()) {
			return Optional.empty();
		}

		// End of loop definition, start of body
		if (!Parenthesis.close(fork)) {
			return Optional.empty();
		}

		// 'for' instructions
		List<Expression> forExpressions = new ArrayList<>();
		if (Curvy.open(fork)) {
			// 'for' Block
			while (!Curvy.close(fork)) {
				Optional<? extends Expression> expr = Expression.build(fork);
				if (expr.isPresent()) {
					forExpressions.add(expr.get());
				} else {
					return Optional.empty();
				}
			}
		} else {
			// Single 'for' expression
			Optional<? extends Expression> expr = Expression.build(fork);
			if (expr.isPresent()) {
				forExpressions.add(expr.get());
			}
		}

		// Commit
		System.out.println("FOR loop detected");
		return Optional.of(new ForBlock(forKeyword.get(), initStatement.get(), evalStatement.get(), iterStatement.get(), forExpressions, fork.commit()));

	}

	// Visit

	@Override
	public <T> T acceptBlockVisitor(BlockVisitor<T> visitor) {
		return visitor.visit(this);
	}

	// Display

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> result = new ArrayList<>();
		result.add(prefix + SyntacticSettings.red() + SyntacticSettings.bold() + "for" + SyntacticSettings.reset() + " (");
		result.addAll(initialisation.fullBreakdown(prefix + "init> "));
		result.addAll(evaluation.fullBreakdown(prefix + "eval> "));
		result.addAll(iteration.fullBreakdown(prefix + "iter> "));
		String bodyPrefix = prefix + "f  ";
		for (Expression expression : body) {
			result.addAll(expression.fullBreakdown(bodyPrefix));
		}
		result.add(prefix + "}");
		return result;
	}

}
