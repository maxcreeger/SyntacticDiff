package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import diff.similarity.SimpleSimilarity.ShowableString;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Reference to a variable in an expression like "obj" in
 * <code>obj.toString()</code>
 */
@Getter
public class VariableReference extends Statement {

	private static final Pattern variableNamePattern = Pattern.compile("\\w+");

	private final ShowableString variableName;

	public VariableReference(ShowableString variableName, CodeLocation location) {
		super(location);
		this.variableName = variableName;
	}

	/**
	 * Attempts to build an reference to a variable.
	 * 
	 * @param inputRef
	 *            the input text (will be mutated if object is built)
	 * @return optionally, an variable reference
	 */
	public static Optional<VariableReference> build(CodeBranch inputRef) {
		Optional<ShowableString> variable = ShowableString.fromPattern(inputRef, variableNamePattern);
		if (!variable.isPresent()) {
			return Optional.empty();
		} else {
			final VariableReference varRef = new VariableReference(variable.get(), variable.get().getLocation());
			return Optional.of(varRef);
		}
	}

	@Override
	public boolean isAssignable() {
		return true;
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> list = new ArrayList<>();
		list.add(variableName.getContent());
		return list;
	}

	@Override
	public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
