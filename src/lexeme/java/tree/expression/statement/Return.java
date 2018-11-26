package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import diff.similarity.SimpleSimilarity.ShowableString;
import lombok.Getter;
import settings.SyntacticSettings;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * A return statement. May return a value, or not.
 */
@Getter
public class Return extends Statement {

	private static final Pattern returnPattern = Pattern.compile("return");

	private final Optional<? extends Statement> returnedValue;
	private final ShowableString returnKeyword;

	public Return(ShowableString returnKeyword, Optional<? extends Statement> returnedValue, CodeLocation location) {
		super(location);
		this.returnKeyword = returnKeyword;
		this.returnedValue = returnedValue;
	}

	/**
	 * Attempts to build a {@link Return} statement.
	 * 
	 * @param input
	 *            the input string (mutated if a return statement is found)
	 * @return optionally, a {@link Return} statement.
	 */
	public static Optional<Return> build(CodeBranch input) {
		CodeBranch fork = input.fork();
		Optional<ShowableString> optReturn = ShowableString.fromPattern(fork, returnPattern);
		if (!optReturn.isPresent()) {
			return Optional.empty();
		}
		Optional<? extends Statement> optStatement = Statement.build(fork);
		return Optional.of(new Return(optReturn.get(), optStatement, fork.commit()));
	}

	@Override
	public boolean isAssignable() {
		return false;
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> returnDisplay = new ArrayList<>();
		if (returnedValue.isPresent()) {
			List<String> retValStr = returnedValue.get().fullBreakdown("");
			for (int i = 0; i < retValStr.size(); i++) {
				if (i == 0) {
					returnDisplay.add(prefix + SyntacticSettings.red() + SyntacticSettings.bold() + "return " + SyntacticSettings.reset() + retValStr.get(i));
				} else {
					returnDisplay.add(prefix + "       " + retValStr.get(i));
				}
			}
		} else {
			returnDisplay.add(prefix + SyntacticSettings.red() + SyntacticSettings.bold() + "return;" + SyntacticSettings.reset());
		}
		return returnDisplay;
	}

	@Override
	public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
