package lexeme.java.tree;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.Showable;
import diff.similarity.SimpleSimilarity.ShowableString;
import lexeme.java.tree.expression.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Defines a class constructor.
 */
@Getter
@AllArgsConstructor
public class Constructor implements Showable {

	private final ShowableString name;
	private final List<Qualifiers> qualifiers;
	private final List<ParameterTypeDeclaration> parameters;
	private final List<Expression> expressions;
	private final CodeLocation location;

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> result = new ArrayList<>();
		// Qualifiers
		String qualif = Qualifiers.toString(qualifiers);
		// Method parameters
		if (parameters.isEmpty()) {
			result.add(prefix + qualif + " " + name.getContent() + "( ) {"); // No constructor parameters
		} else {
			result.add(prefix + qualif + " " + name.getContent() + "(");
			for (ParameterTypeDeclaration param : parameters) {
				result.addAll(param.fullBreakdown(prefix + "p   ")); // list constructor parameters
			}
			result.add(prefix + ") {");
		}
		// Method body
		String bodyPrefix = prefix + "c  ";
		for (Expression expr : expressions) {
			result.addAll(expr.fullBreakdown(bodyPrefix));
		}
		result.add(prefix + "}");
		return result;
	}

	public String getSurroundingClassName() {
		return name.getContent();
	}

}
