package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a series of '.' statement acting on other statements like
 * <code>new Object().toString().someField</code>. <br>
 * The dot may represent field accesses or method invocations, or Class name
 * hierarchy (<code>Type.SubType.staticMethod()</code>)
 */
@Getter
public class ChainedAccess extends Statement {
	private static final Pattern beginInvocation = Pattern.compile("\\.");

	private final List<Statement> statements;

	public ChainedAccess(List<Statement> statements, CodeLocation location) {
		super(location);
		this.statements = statements;
	}

	/**
	 * Attempts to build a {@link ChainedAccess} from a mutable input String.
	 * 
	 * @param source
	 *            an identified subject of the access
	 * @param inputRef
	 *            the input string. If a match is found, its text representation
	 *            is removed from the input string
	 * @return optionally, a {@link ChainedAccess}
	 */
	public static Optional<ChainedAccess> build(Statement source, CodeBranch inputRef) {
		CodeBranch defensiveCopy = inputRef.fork();
		List<Statement> statements = new ArrayList<>();
		statements.add(source);
		while (startChaining(defensiveCopy)) { // Expect a '.'
			// Attempt to find method invocation
			Optional<MethodInvocation> optionalMethodInvocation = MethodInvocation.build(defensiveCopy);
			if (optionalMethodInvocation.isPresent()) {
				statements.add(optionalMethodInvocation.get());
				continue;
			}

			// Attempt to find field access
			Optional<VariableReference> optionalVarRef = VariableReference.build(defensiveCopy);
			if (optionalVarRef.isPresent()) {
				statements.add(optionalVarRef.get());
				continue;
			}
		}

		// Nothing found, end of chain
		if (statements.size() <= 1) {
			return Optional.empty(); // No chaining found after source
		} else {

			return Optional.of(new ChainedAccess(statements, defensiveCopy.commit()));
		}
	}

	private static boolean startChaining(CodeBranch input) {
		Matcher beginMatcher = beginInvocation.matcher(input.getRest());
		if (!beginMatcher.lookingAt()) {
			return false;
		} else {
			input.advance(beginMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(input);
			return true;
		}
	}

	@Override
	public boolean isAssignable() {
		return statements.get(statements.size() - 1).isAssignable(); // Last statement would be the target of the assignment
	}

	// Display

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> result = new ArrayList<>();

		// Source statement
		final Statement firstStatement = statements.get(0);
		final List<String> firstStatementShow = firstStatement.fullBreakdown("");
		final String firstStatementLastLine = firstStatementShow.get(firstStatementShow.size() - 1);
		for (int lineNum = 0; lineNum < firstStatementShow.size() - 1; lineNum++) {
			result.add(prefix + firstStatementShow.get(lineNum));
		}
		result.add(prefix + firstStatementLastLine);
		String alignment = new String(new char[firstStatementLastLine.length()]).replace("\0", " ");

		// Subsequent chained calls
		for (int statementNum = 1; statementNum < statements.size(); statementNum++) {
			Statement statement = statements.get(statementNum);
			final List<String> statementShow = statement.fullBreakdown("");
			final String statementFirstLine = statementShow.get(0);
			result.add(prefix + alignment + "." + statementFirstLine);
			for (int lineNum = 1; lineNum < statementShow.size(); lineNum++) {
				result.add(prefix + alignment + statementShow.get(lineNum));
			}
		}
		return result;
	}

	@Override
	public List<String> nativeFormat(String prefix) {
		List<String> result = new ArrayList<>();
		String firstLineContent = "";
		String alternateAlignment = "";
		String separator = "";
		for (int statementNum = 0; statementNum < statements.size(); statementNum++) {
			Statement statement = statements.get(statementNum);
			final List<String> statementShow = statement.nativeFormat("");
			if (statementShow.size() == 1) {
				firstLineContent += separator + statementShow.get(0);
				separator = ".";
				alternateAlignment += new String(new char[statementShow.get(0).length() + 1]).replace("\0", " ");
			} else {
				// 1st line
				result.add(prefix + firstLineContent + statementShow.get(0));
				// regular content
				for (int lineNum = 1; lineNum < statementShow.size() - 1; lineNum++) {
					result.add(prefix + alternateAlignment + statementShow.get(lineNum));
				}
				//last line (becomes first for the following statements)
				firstLineContent = alternateAlignment + statementShow.get(statementShow.size() - 1);
				alternateAlignment = alternateAlignment + new String(new char[statementShow.get(statementShow.size() - 1).length()]).replace("\0", " ");
			}
		}
		result.add(prefix + firstLineContent);
		return result;
	}

	@Override
	public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
