package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import diff.similarity.SimpleSimilarity.ShowableString;
import lexeme.java.tree.ParameterPassing;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Method invocation like obj.doSomething().
 */
@Getter
public class MethodInvocation extends Statement {

	private static final Pattern methodNamePattern = Pattern.compile("\\w+");

	private final ShowableString methodName;
	private final ParameterPassing arguments;

	public MethodInvocation(ShowableString methodName, ParameterPassing arguments, CodeLocation location) {
		super(location);
		this.methodName = methodName;
		this.arguments = arguments;
	}

	/**
	 * Attempts to build a method invocation.
	 * 
	 * @param inputRef
	 *            the input text (will be mutated if object is built)
	 * @return optionally, a {@link MethodInvocation}
	 */
	public static Optional<MethodInvocation> build(CodeBranch inputRef) {
		CodeBranch defensiveCopy = inputRef.fork();

		// Method name
		Optional<ShowableString> methodName = ShowableString.fromPattern(defensiveCopy, methodNamePattern);
		if (!methodName.isPresent()) {
			return Optional.empty();
		}

		// Arguments in parenthesis
		Optional<ParameterPassing> arguments = ParameterPassing.build(defensiveCopy);
		if (!arguments.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(new MethodInvocation(methodName.get(), arguments.get(), defensiveCopy.commit()));
	}

	@Override
	public boolean isAssignable() {
		return false;
	}

	// display

	@Override
	public List<String> fullBreakdown(String prefix) {
		final List<String> showParameters = arguments.fullBreakdown(prefix);
		if (showParameters.size() == 0) {
			return Arrays.asList(prefix + methodName.getContent() + "( )");
		} else {
			int nameWidth = methodName.getContent().length();
			String nameOffset = new String(new char[nameWidth]).replace("\0", " ");
			List<String> result = new ArrayList<>();
			result.add(prefix + methodName + "(");
			for (int i = 0; i < showParameters.size(); i++) {
				result.add(prefix + nameOffset + " " + showParameters.get(i));
			}
			result.add(prefix + nameOffset + ")");
			return result;
		}
	}

	@Override
	public List<String> nativeFormat(String prefix) {
		final List<String> showParameters = arguments.nativeFormat(prefix);
		if (showParameters.size() == 0) {
			return Arrays.asList(prefix + methodName + "()");
		} else {
			int nameWidth = methodName.getContent().length();
			String nameOffset = new String(new char[nameWidth + 1]).replace("\0", " ");
			List<String> result = new ArrayList<>();
			for (int i = 0; i < showParameters.size(); i++) {
				result.add(prefix + (i == 0 ? methodName + "(" : nameOffset) + showParameters.get(i) + (i == showParameters.size() - 1 ? ")" : ","));
			}
			return result;
		}
	}

	@Override
	public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
