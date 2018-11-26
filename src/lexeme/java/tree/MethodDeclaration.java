package lexeme.java.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import diff.complexity.Showable;
import diff.similarity.SimpleSimilarity.ShowableString;
import lexeme.java.intervals.Curvy;
import lexeme.java.tree.Qualifiers.JavaQualifier;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.statement.VariableReference;
import lexer.Structure;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * A Complete method declaration, with qualifiers, return type, name, parameter
 * types, and body.
 */
@AllArgsConstructor
@Getter
public class MethodDeclaration implements Showable, Structure<JavaGrammar> {

	private final List<Qualifiers> qualifiers;
	private final ClassName returnType;
	private final ShowableString name;
	private final List<ParameterTypeDeclaration> parameters;
	private final List<Expression> expressions;
	private final CodeLocation location;

	/**
	 * Attempts to build a {@link MethodDeclaration}.
	 * 
	 * @param inputRef
	 *            the input text (is mutated if a method declaration is built)
	 * @return optionally, a {@link MethodDeclaration}
	 */
	public static Optional<MethodDeclaration> build(CodeBranch inputRef) {
		CodeBranch defensiveCopy = inputRef.fork();

		// Method properties (name, qualifiers, hierarchy)
		List<Qualifiers> qualifiers = Qualifiers.searchQualifiers(defensiveCopy);

		// Return type
		Optional<ClassName> returnType = ClassName.build(defensiveCopy);
		if (!returnType.isPresent()) {
			return Optional.empty();
		}

		// Method name
		Optional<VariableReference> methodName = VariableReference.build(defensiveCopy);
		if (!methodName.isPresent()) {
			return Optional.empty();
		}

		// Method parameters
		Optional<List<ParameterTypeDeclaration>> parameters = ParameterTypeDeclaration.buildSeries(defensiveCopy);
		if (!parameters.isPresent()) {
			return Optional.empty();
		}
		ShowableString methodNameString = new ShowableString(methodName.get().getVariableName().getContent(), methodName.get().getLocation());

		// Begin method body
		if (!Curvy.open(defensiveCopy)) {
			return Optional.empty();
		}

		// Explore Method body
		List<Expression> expressions = new ArrayList<>();
		while (!Curvy.close(defensiveCopy)) {

			// Try to find an expression
			Optional<? extends Expression> optional = Expression.build(defensiveCopy);
			if (optional.isPresent()) {
				Expression varDec = optional.get();
				expressions.add(varDec);
			}
		}

		// System.out.println("Found method declaration : " + methodName.get().getVariableName());
		return Optional.of(new MethodDeclaration(qualifiers, returnType.get(), methodNameString, parameters.get(), expressions, defensiveCopy.commit()));
	}

	public List<JavaQualifier> getQualifierEnums() {
		return qualifiers.stream().map(quali -> quali.qualifier).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return String.join("\n", fullBreakdown(""));
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> result = new ArrayList<>();
		// Qualifiers
		List<String> qualifierBreakdown = Qualifiers.fullBreakdown(qualifiers);
		result.addAll(qualifierBreakdown.stream().map(q -> prefix + q).collect(Collectors.toList()));
		int qualiWidth = qualifierBreakdown.stream().mapToInt(String::length).max().orElse(0);
		String qualiOffset = new String(new char[qualiWidth + 1]).replace("\0", " ");

		// Return type
		List<String> returnBreakdown = returnType.fullBreakdown("");
		result.addAll(returnBreakdown.stream().map(line -> prefix + qualiOffset + line).collect(Collectors.toList()));
		int returnTypeWidth = returnBreakdown.stream().mapToInt(String::length).max().orElse(0);
		String returnTypeOffset = new String(new char[returnTypeWidth + 1]).replace("\0", " ");

		// Method parameters
		if (parameters.isEmpty()) {
			result.add(prefix + qualiOffset + returnTypeOffset + name + "( ) {"); // No method parameters
		} else {
			int nameWidth = name.getContent().length();
			String nameOffset = new String(new char[nameWidth + 1]).replace("\0", " ");
			result.add(prefix + qualiOffset + returnTypeOffset + name + "(");
			for (ParameterTypeDeclaration param : parameters) {
				result.addAll(param.fullBreakdown(prefix + qualiOffset + returnTypeOffset + nameOffset)); // list method parameters
			}
			result.add(prefix + qualiOffset + returnTypeOffset + ") {");
		}

		// Method body
		String bodyPrefix = prefix + "m  b  ";
		for (Expression expr : expressions) {
			result.addAll(expr.fullBreakdown(bodyPrefix));
		}
		result.add(prefix + "}");
		return result;
	}

	@Override
	public List<String> nativeFormat(String prefix) {
		List<String> result = new ArrayList<>();
		// Qualifiers
		String qualif = Qualifiers.toString(qualifiers);
		// Method parameters
		if (parameters.isEmpty()) {
			result.add(prefix + qualif + " " + returnType.toString() + " " + name + "( ) {"); // No method parameters
		} else {
			result.add(prefix + qualif + " " + returnType.toString() + " " + name + "(");
			for (ParameterTypeDeclaration param : parameters) {
				result.addAll(param.nativeFormat(prefix + "m  p  ")); // list method parameters
			}
			result.add(prefix + "m  ) {");
		}
		// Method body
		String bodyPrefix = prefix + "m  b  ";
		for (Expression expr : expressions) {
			result.addAll(expr.nativeFormat(bodyPrefix));
		}
		result.add(prefix + "}");
		return result;
	}
}
