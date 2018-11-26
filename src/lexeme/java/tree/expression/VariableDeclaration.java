package lexeme.java.tree.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import diff.similarity.SimpleSimilarity.ShowableString;
import lexeme.java.tree.ClassName;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.Qualifiers;
import lexeme.java.tree.Qualifiers.JavaQualifier;
import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.VariableReference;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * A Variable (or field) declaration
 */
@Getter
public class VariableDeclaration extends Expression {

	private static final Pattern assignmentPattern = Pattern.compile("=");

	private final List<Qualifiers> qualifiers;
	private final ClassName type;
	private final ShowableString name;
	private final Optional<? extends Statement> initialAssignement;

	public VariableDeclaration(List<Qualifiers> qualifiers, ClassName type, ShowableString name, Optional<? extends Statement> initialAssignement,
		CodeLocation location) {
		super(location);
		this.qualifiers = qualifiers;
		this.type = type;
		this.name = name;
		this.initialAssignement = initialAssignement;
	}

	public static Optional<VariableDeclaration> build(CodeBranch inputRef) {
		CodeBranch fork = inputRef.fork();

		// Search qualifiers
		List<Qualifiers> qualifiers = Qualifiers.searchQualifiers(fork);

		// Search type
		Optional<ClassName> optionalClassName = ClassName.build(fork);
		if (!optionalClassName.isPresent()) {
			return Optional.empty();
		}
		ClassName className = optionalClassName.get();

		// Search variableName
		Optional<VariableReference> varName = VariableReference.build(fork);
		if (!varName.isPresent()) {
			return Optional.empty();
		}

		// Either this is an empty declaration, or there may be an assignment?
		Optional<? extends Statement> assignement;
		if (findAssignment(fork)) {
			assignement = Statement.build(fork);
		} else {
			assignement = Optional.empty();
		}

		// Variable declaration is valid, let's commit the changes to the input reference
		return Optional.of(new VariableDeclaration(qualifiers, className,
			new ShowableString(varName.get().getVariableName().getContent(), varName.get().getLocation()), assignement, fork.commit()));
	}

	private static boolean findAssignment(CodeBranch input) {
		Matcher nameMatcher = assignmentPattern.matcher(input.getRest());
		if (!nameMatcher.lookingAt()) {
			return false;
		}
		input.advance(nameMatcher.end());
		JavaWhitespace.skipWhitespaceAndComments(input);
		return true;
	}

	public List<JavaQualifier> getQualifierEnums() {
		return qualifiers.stream().map(quali -> quali.getQualifier()).collect(Collectors.toList());
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> result = new ArrayList<>();

		// Qualifiers
		List<String> qualifierBreakdown = Qualifiers.fullBreakdown(qualifiers);
		result.addAll(qualifierBreakdown.stream().map(q -> prefix + q).collect(Collectors.toList()));
		int qualiWidth = qualifierBreakdown.stream().mapToInt(String::length).max().orElse(0);
		String qualiOffset = new String(new char[qualiWidth + (qualifierBreakdown.isEmpty() ? 0 : 1)]).replace("\0", " ");

		// Var Type
		List<String> typeBreakdown = type.fullBreakdown("");
		result.addAll(typeBreakdown.stream().map(t -> prefix + qualiOffset + t).collect(Collectors.toList()));
		int typeWidth = typeBreakdown.stream().mapToInt(String::length).max().orElse(0);
		String typeOffset = new String(new char[typeWidth + (typeBreakdown.isEmpty() ? 0 : 1)]).replace("\0", " ");

		// Var name
		result.add(prefix + qualiOffset + typeOffset + name.toString());
		String nameOffset = new String(new char[name.toString().length()]).replace("\0", " ");

		// Var type
		if (initialAssignement.isPresent()) {
			String alignment = prefix + qualiOffset + typeOffset + nameOffset;
			List<String> assignmentLines = initialAssignement.get().fullBreakdown("");
			for (int i = 0; i < assignmentLines.size(); i++) {
				if (i == 0) {
					result.add(alignment + " = " + assignmentLines.get(i)); // First line has LHS
				} else {
					result.add(alignment + "   " + assignmentLines.get(i)); // other lines omit LHS
				}
			}
		}
		return result;
	}

	@Override
	public List<String> nativeFormat(String prefix) {
		List<String> result = new ArrayList<>();

		// Qualifiers
		String qualifierString = Qualifiers.toString(qualifiers); // guaranteed 1-liner
		qualifierString = qualifierString.isEmpty() ? qualifierString : qualifierString + " ";

		// Var Type
		List<String> typeBreakdown = type.nativeFormat(""); // assuming 1-liner is provided.
		String typeString = typeBreakdown.get(0);

		// Var name
		String nameString = name.toString();

		// Var type
		if (initialAssignement.isPresent()) {
			String declaration = qualifierString + typeString + " " + nameString + " = ";
			String alignment = new String(new char[declaration.length()]).replace("\0", " ");
			List<String> assignmentLines = initialAssignement.get().nativeFormat("");
			for (int i = 0; i < assignmentLines.size(); i++) {
				if (i == 0) {
					result.add(prefix + declaration + assignmentLines.get(i)); // First line has LHS
				} else {
					result.add(prefix + alignment + assignmentLines.get(i)); // other lines omit LHS
				}
			}
		} else {
			result.add(prefix + qualifierString + typeString + " " + nameString);
		}
		return result;
	}

	@Override
	public <T> T acceptExpressionVisitor(ExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
