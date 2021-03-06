package lexeme.java.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lexeme.java.intervals.Parenthesis;
import lexeme.java.tree.expression.statement.VariableReference;
import lexer.Structure;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Declaration of parameters type declarations like in {@code List<String>}.
 */
@Getter
@AllArgsConstructor
public class ParameterTypeDeclaration implements Showable, Structure<JavaGrammar> {

	private static final Pattern separatorPattern = Pattern.compile(",");

	private final List<Qualifiers> qualifiers;
	private final ClassName type;
	private final VariableReference name;
	private final CodeLocation location;

	/**
	 * Attempts to Build a number of sequential parameter declarations
	 * 
	 * @param inputRef
	 *            the input text
	 * @return optionally, a {@link List} of {@link ParameterTypeDeclaration}
	 *         (never an empty list)
	 */
	public static Optional<List<ParameterTypeDeclaration>> buildSeries(CodeBranch inputRef) {
		CodeBranch defensiveCopy = inputRef.fork();

		// Open parenthesis
		if (!Parenthesis.open(defensiveCopy)) {
			return Optional.empty();
		}

		List<ParameterTypeDeclaration> declaredParameters = new ArrayList<>();
		while (true) {

			// Attempt to read Parameter Declaration
			Optional<ParameterTypeDeclaration> paramDeclaration = build(defensiveCopy);
			if (!paramDeclaration.isPresent()) {
				break;
			}
			declaredParameters.add(paramDeclaration.get());

			// Attempt to have a delimiter between parameters?
			if (delimiter(defensiveCopy)) {
				continue; // Yay! another one!
			} else {
				break; // Hmm it's over
			}
		}

		// Attempt close parenthesis
		if (!Parenthesis.close(defensiveCopy)) {
			throw new RuntimeException("Expecting parameters or closing parenthesis in method invocation");
		}

		defensiveCopy.commit(); // it's ok to lose the Location of the full list
		return Optional.of(declaredParameters);
	}

	/**
	 * Attempts to build a {@link ParameterTypeDeclaration} from an input text
	 * 
	 * @param inputRef
	 *            the mutable input (is modified if a parameter declaration is
	 *            indeed found)
	 * @return optionally, a {@link ParameterTypeDeclaration}
	 */
	public static Optional<ParameterTypeDeclaration> build(CodeBranch inputRef) {
		CodeBranch defensiveCopy = inputRef.fork();

		// Search qualifiers
		List<Qualifiers> qualifiers = new ArrayList<>();
		while (true) {
			Optional<Qualifiers> optionalQual = Qualifiers.detect(defensiveCopy);
			if (optionalQual.isPresent()) {
				qualifiers.add(optionalQual.get());
			} else {
				break;
			}
		}

		// Search type
		Optional<ClassName> optionalClassName = ClassName.build(defensiveCopy);
		if (!optionalClassName.isPresent()) {
			return Optional.empty();
		}
		ClassName className = optionalClassName.get();

		// Search variableName
		Optional<VariableReference> parameter = VariableReference.build(defensiveCopy);
		if (!parameter.isPresent()) {
			return Optional.empty();
		}

		// Variable declaration is valid, let's commit the changes to the input reference
		return Optional.of(new ParameterTypeDeclaration(qualifiers, className, parameter.get(), defensiveCopy.commit()));
	}

	private static boolean delimiter(CodeBranch code) {
		Matcher separator = separatorPattern.matcher(code.getRest());
		if (!separator.lookingAt()) {
			return false;
		}
		code.advance(separator.end());
		JavaWhitespace.skipWhitespaceAndComments(code);
		return true;
	}

	/**
	 * Compares these parameter types to others
	 * 
	 * @param other
	 *            the others
	 * @return true if they are the same
	 */
	public boolean compare(ParameterTypeDeclaration other) {
		if (!getName().equals(other.getName())) {
			return false;
		}
		if (!Qualifiers.compare(getQualifiers(), other.getQualifiers())) {
			return false;
		}
		if (!getType().compare(other.getType())) {
			return false;
		}
		return true;
	}

	/**
	 * Compares a series of type parameters
	 * 
	 * @param parametersA
	 *            a series of type parameters
	 * @param parametersB
	 *            a series of type parameters
	 * @return true if they are exactly the same
	 */
	public static boolean compare(List<ParameterTypeDeclaration> parametersA, List<ParameterTypeDeclaration> parametersB) {
		if (parametersA.size() != parametersB.size()) {
			return false;
		}
		for (int num = 0; num < parametersA.size(); num++) {
			ParameterTypeDeclaration paramA = parametersA.get(num);
			ParameterTypeDeclaration paramB = parametersB.get(num);
			if (!paramA.compare(paramB)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		final String qualifierString = Qualifiers.toString(qualifiers);
		return (qualifierString.isEmpty() ? "" : qualifierString + " ") + type.toString() + " " + name.getVariableName();
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		final ArrayList<String> result = new ArrayList<>();
		result.add(prefix + this.toString());
		return result;
	}

}
