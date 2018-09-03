package lexeme.java.tree;

import java.util.ArrayList;
import java.util.List;

import lexeme.java.tree.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;
import diff.complexity.Showable;

/**
 * Defines a class constructor.
 */
@Getter
@AllArgsConstructor
public class Constructor implements Showable {

	private final String surroundingClassName;
	private final List<Qualifiers> qualifiers;
	private final List<ParameterTypeDeclaration> parameters;
	private final List<Expression> expressions;

	@Override
	public List<String> show(String prefix) {
		List<String> result = new ArrayList<>();
		// Qualifiers
		String qualif = Qualifiers.toString(qualifiers);
		// Method parameters
		if (parameters.isEmpty()) {
			result.add(prefix + qualif + " " + surroundingClassName + "( ) {"); // No constructor parameters
		} else {
			result.add(prefix + qualif + " " + surroundingClassName + "(");
			for (ParameterTypeDeclaration param : parameters) {
				result.addAll(param.show(prefix + "p   ")); // list constructor parameters
			}
			result.add(prefix + ") {");
		}
		// Method body
		String bodyPrefix = prefix + "c  ";
		for (Expression expr : expressions) {
			result.addAll(expr.show(bodyPrefix));
		}
		result.add(prefix + "}");
		return result;
	}

}