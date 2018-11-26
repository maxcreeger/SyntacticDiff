package lexeme.java.tree.expression.blocks.trycatchfinally;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import diff.similarity.SimpleSimilarity.ShowableString;
import lexeme.java.intervals.Curvy;
import lexeme.java.intervals.Parenthesis;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.VariableDeclaration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import settings.SyntacticSettings;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a try block. May contain resources, must have a body of
 * expressions.
 */
@Getter
@AllArgsConstructor
public final class TryBlock implements Showable {

	private static final Pattern TRY = Pattern.compile("try");
	private static final Pattern RESOURCE_SEPARATOR = Pattern.compile(";");

	private final ShowableString tryKeyword;
	private final List<VariableDeclaration> tryWithResources;
	private final List<Expression> tryExpressions;
	private final CodeLocation location;

	protected static Optional<TryBlock> build(CodeBranch input) {
		CodeBranch fork = input.fork();

		// Match 'try' keyword
		Optional<ShowableString> tryKeyword = ShowableString.fromPattern(fork, TRY);
		if (!tryKeyword.isPresent()) {
			return Optional.empty();
		}

		List<VariableDeclaration> tryWithResources = new ArrayList<>();
		// Begin try-with-resources
		if (Parenthesis.open(fork)) {
			// Resources declaration
			do {
				Optional<VariableDeclaration> varDecla = VariableDeclaration.build(fork);
				if (varDecla.isPresent()) {
					tryWithResources.add(varDecla.get());
				} else {
					return Optional.empty(); // Failed to find a resource declaration
				}
			} while (findResourceSeparator(fork));

			// End of try resources
			if (!Parenthesis.close(fork)) {
				return Optional.empty(); // resources list not closed
			}
		}

		// 'try' Block - begin
		List<Expression> tryExpressions = new ArrayList<>();
		if (!Curvy.open(fork)) {
			return Optional.empty();
		}

		// 'try' Block - content
		while (!Curvy.close(fork)) {
			Optional<? extends Expression> expr = Expression.build(fork);
			if (expr.isPresent()) {
				tryExpressions.add(expr.get());
			} else {
				return Optional.empty();
			}
		}

		// System.out.println(">Try block detected");
		return Optional.of(new TryBlock(tryKeyword.get(), tryWithResources, tryExpressions, fork.commit()));
	}

	private static boolean findResourceSeparator(CodeBranch input) {
		Matcher separatorMatcher = RESOURCE_SEPARATOR.matcher(input.getRest());
		if (!separatorMatcher.lookingAt()) {
			return false;
		} else {
			input.advance(separatorMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(input);
			return true;
		}
	}

	// Display

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> total = new ArrayList<>();

		if (tryWithResources.isEmpty()) {
			total.add(prefix + SyntacticSettings.red() + SyntacticSettings.bold() + "try" + SyntacticSettings.reset() + " {");
		} else {
			// Resources (optional)
			total.add(prefix + SyntacticSettings.red() + SyntacticSettings.bold() + "try" + SyntacticSettings.reset() + " (");
			for (VariableDeclaration tryVar : tryWithResources) {
				total.addAll(tryVar.fullBreakdown(prefix + "r  "));
			}
			total.add(prefix + ") {");
		}

		// Body
		for (Expression expression : tryExpressions) {
			total.addAll(expression.fullBreakdown(prefix + "y  "));
		}

		// Close off
		total.add(prefix + "}");
		return total;
	}
}
