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
 * Represents an if block, with a conditional statement and a body. May have
 * else expressions.
 */
@Getter
public class IfBlock extends AbstractBlock {

	private static final Pattern ifPattern = Pattern.compile("if");
	private static final Pattern elsePattern = Pattern.compile("else");

	private final ShowableString ifKeyword;
	private final Statement condition;
	private final List<Expression> thenExpressions;
	private final Optional<ShowableString> elseKeyword;
	private final List<Expression> elseExpressions;

	public IfBlock(ShowableString ifKeyword, Statement condition, List<Expression> thenExpressions, Optional<ShowableString> elseKeyword,
		List<Expression> elseExpressions, CodeLocation location) {
		super(location);
		this.ifKeyword = ifKeyword;
		this.condition = condition;
		this.thenExpressions = thenExpressions;
		this.elseKeyword = elseKeyword;
		this.elseExpressions = elseExpressions;
	}

	/**
	 * Attempts to build a {@link IfBlock}
	 * 
	 * @param inputRef
	 *            the input text (is modified if the block is built)
	 * @return optionally, the block
	 */
	public static Optional<IfBlock> build(CodeBranch inputRef) {
		CodeBranch fork = inputRef.fork();

		// Match 'if' keyword
		Optional<ShowableString> ifKeyword = ShowableString.fromPattern(fork, ifPattern);
		if (!ifKeyword.isPresent()) {
			return Optional.empty();
		}

		// Begin condition
		if (!Parenthesis.open(fork)) {
			return Optional.empty();
		}
		// Conditional statement
		Optional<? extends Statement> optStatement = Statement.build(fork);
		if (!optStatement.isPresent()) {
			return Optional.empty();
		}

		// end of conditional statement, start of body
		if (!Parenthesis.close(fork)) {
			return Optional.empty();
		}

		// 'Then' instructions
		List<Expression> thenExpressions = new ArrayList<>();
		if (Curvy.open(fork)) {
			// 'Then' Block
			while (!Curvy.close(fork)) {
				Optional<? extends Expression> expr = Expression.build(fork);
				if (expr.isPresent()) {
					thenExpressions.add(expr.get());
				}
			}
		} else {
			// Single 'then' expression
			Optional<? extends Expression> expr = Expression.build(fork);
			if (expr.isPresent()) {
				thenExpressions.add(expr.get());
			}
		}

		// Maybe else block?
		Optional<ShowableString> elseKeyword = ShowableString.fromPattern(fork, elsePattern);
		List<Expression> elseExpressions = new ArrayList<>();
		if (elseKeyword.isPresent()) {
			if (Curvy.open(fork)) {
				// 'else' Block
				while (!Curvy.close(fork)) {
					Optional<? extends Expression> expr = Expression.build(fork);
					if (expr.isPresent()) {
						elseExpressions.add(expr.get());
					}
				}
			} else {
				// Single 'else' expression
				Optional<? extends Expression> expr = Expression.build(fork);
				if (expr.isPresent()) {
					elseExpressions.add(expr.get());
				}
			}
		}

		// Commit
		// System.out.println("if[then][else] block detected");
		return Optional.of(new IfBlock(ifKeyword.get(), optStatement.get(), thenExpressions, elseKeyword, elseExpressions, fork.commit()));

	}

	@Override
	public <T> T acceptBlockVisitor(BlockVisitor<T> visitor) {
		return visitor.visit(this);
	}

	// Display

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> result = new ArrayList<>();

		// IF
		List<String> conditionShow = condition.fullBreakdown("");
		if (conditionShow.size() == 1) {
			result.add(prefix + "if (" + conditionShow.get(0) + " ) {");
		} else {
			result.add(prefix + "if (");
			for (String line : conditionShow) {
				result.add(prefix + "?  " + line);
			}
			result.add(prefix + ") {");
		}

		// THEN
		String thenPrefix = prefix + "n  ";
		for (Expression expression : thenExpressions) {
			result.addAll(expression.fullBreakdown(thenPrefix));
		}

		// ELSE
		if (elseExpressions != null && !elseExpressions.isEmpty()) {
			result.add(prefix + "} else { ");
			String elsePrefix = prefix + "e  ";
			for (Expression expression : elseExpressions) {
				result.addAll(expression.fullBreakdown(elsePrefix));
			}
		}
		result.add(prefix + "}");
		return result;
	}

	@Override
	public List<Expression> getBody() {
		return thenExpressions;
	}

}
