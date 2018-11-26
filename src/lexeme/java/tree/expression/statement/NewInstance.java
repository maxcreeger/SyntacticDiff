package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.ClassName;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.ParameterPassing;
import lombok.Getter;
import settings.SyntacticSettings;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a call to a constructor "new Object(arguments...)".
 */
@Getter
public class NewInstance extends Statement {

	private static final Pattern newPattern = Pattern.compile("new\\s+");

	private final ClassName className;
	private final Optional<ParameterPassing> constructorArguments;
	private final Optional<ArrayDeclaration> arrayDeclaration;

	public NewInstance(ClassName className, Optional<ParameterPassing> constructorArguments, Optional<ArrayDeclaration> arrayDeclaration,
		CodeLocation location) {
		super(location);
		this.className = className;
		this.constructorArguments = constructorArguments;
		this.arrayDeclaration = arrayDeclaration;
	}

	public static Optional<NewInstance> build(CodeBranch input) {
		CodeBranch fork = input.fork();
		Matcher newMatcher = newPattern.matcher(fork.getRest());
		if (!newMatcher.lookingAt()) {
			return Optional.empty();
		}

		// Reserved keyword 'new' has been found
		fork.advance(newMatcher.end());
		JavaWhitespace.skipWhitespaceAndComments(fork);

		Optional<ClassName> className = ClassName.build(fork);
		if (!className.isPresent()) {
			throw new RuntimeException("Expecting Constructor after 'new' keyword");
		}

		// Try to make the above an array
		Optional<ArrayDeclaration> arrayDeclaration = ArrayDeclaration.build(fork);
		if (arrayDeclaration.isPresent()) {
			// Cannot be a constructor call, now
			return Optional.of(new NewInstance(className.get(), Optional.empty(), arrayDeclaration, fork.commit()));
		} else {
			Optional<ParameterPassing> params = ParameterPassing.build(fork);
			if (params.isPresent()) {
				return Optional.of(new NewInstance(className.get(), params, Optional.empty(), fork.commit()));
			} else {
				return Optional.empty();
			}
		}
	}

	@Override
	public boolean isAssignable() {
		return false;
	}

	// display

	@Override
	public List<String> fullBreakdown(String prefix) {
		final String newFormatted = SyntacticSettings.red() + SyntacticSettings.bold() + "new" + SyntacticSettings.reset();
		if (constructorArguments.isPresent()) {
			// Is constructor call
			final List<String> showArguments = constructorArguments.get().fullBreakdown("");
			if (showArguments.size() == 0) {
				return Arrays.asList(prefix + newFormatted + " " + className + "( )");
			} else if (showArguments.size() == 1) {
				return Arrays.asList(prefix + newFormatted + " " + className + "( " + showArguments.get(0) + " )");
			} else {
				List<String> result = new ArrayList<>();
				result.add(prefix + newFormatted + " " + className + "(");
				for (String line : showArguments) {
					result.add(prefix + "n  " + line);
				}
				result.add(prefix + ")");
				return result;
			}
		} else {
			// Is array creation
			List<String> showArray = arrayDeclaration.get().fullBreakdown("");
			if (showArray.size() == 0) {
				return Arrays.asList(prefix + newFormatted + " " + className + "( )");
			} else if (showArray.size() == 1) {
				return Arrays.asList(prefix + newFormatted + " " + className + "( " + showArray.get(0) + " )");
			} else {
				List<String> result = new ArrayList<>();
				result.add(prefix + newFormatted + " " + className + "[");
				for (String line : showArray) {
					result.add(prefix + "n  " + line);
				}
				result.add(prefix + "]");
				return result;
			}
		}
	}

	@Override
	public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
