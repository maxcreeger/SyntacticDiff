package lexeme.java.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import diff.similarity.SimpleSimilarity.ShowableString;
import lexeme.java.intervals.Curvy;
import lexeme.java.tree.Qualifiers.JavaQualifier;
import lexeme.java.tree.expression.EmptyExpression;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.VariableDeclaration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * A complete class declaration, with:
 * <ul>
 * <li>qualifiers</li>
 * <li>nested Sub-Parameters</li>
 * <li>super Class</li>
 * <li>extends Class</li>
 * <li>implemented Interfaces</li>
 * <li>static fields</li>
 * <li>instance fields</li>
 * <li>static methods</li>
 * <li>instance methods</li>
 * <li>static inner classes</li>
 * <li>inner classes</li>
 * </ul>
 */
@AllArgsConstructor
@Getter
public class ClassDeclaration implements JavaSyntax {

	private static final Pattern classKeyword = Pattern.compile("class");
	private static final Pattern classNamePattern = Pattern.compile("\\w+");
	private static final Pattern extendsPattern = Pattern.compile("extends");
	private static final Pattern implementsPattern = Pattern.compile("implements");
	private static final Pattern beginChevronPattern = Pattern.compile("<");
	private static final Pattern separatorPattern = Pattern.compile("\\s*,\\s*");
	private static final Pattern endChevronPattern = Pattern.compile(">");

	// Declarators
	private final List<Qualifiers> qualifiers;
	private final ShowableString className;
	private final List<ClassName> nestedSubParameters; // Null for N/A, empty for Type inference
	private final Optional<ClassName> extendsClass;
	private final List<ClassName> implementedInterfaces;
	// Body
	private final List<VariableDeclaration> staticFields;
	private final List<VariableDeclaration> fields;
	private final List<Constructor> constructors;
	private final List<MethodDeclaration> staticMethods;
	private final List<MethodDeclaration> methods;
	private final List<ClassDeclaration> innerClasses;
	private final List<ClassDeclaration> staticInnerClasses;
	private final CodeLocation location;

	/**
	 * Attempts to build a {@link ClassDeclaration} from the input.
	 * 
	 * @param inputRef
	 *            a mutable text, if a class definition is found the text
	 *            declaration is removed.
	 * @return optionally, a class declaration
	 */
	public static Optional<ClassDeclaration> build(CodeBranch input) {
		CodeBranch fork = input.fork();

		// Class properties (name, qualifiers, hierarchy)
		List<Qualifiers> qualifiers = Qualifiers.searchQualifiers(fork);

		// Class keyword
		if (!expectClassKeyword(fork)) {
			return Optional.empty();
		}

		// Class name
		Optional<ShowableString> className = ShowableString.fromPattern(fork, classNamePattern);
		if (!className.isPresent()) {
			throw new RuntimeException("Missing class name in class definition");
		}

		// Attempt to find some Type parameters
		List<ClassName> typeParameters = null;
		if (openChevron(fork)) {
			typeParameters = new ArrayList<>(); // Non null!
			while (findParameter(fork, typeParameters)) {
				// Attempt to find a separator between parameters
				matchSeparator(fork);
			}
			expectEndChevron(fork);
		}

		// Attempt to find extends
		Optional<ClassName> extendsClass = ClassName.find(fork, extendsPattern);

		// Attempt to find implements
		List<ClassName> implementedInterfaces = findInterfaces(fork);

		// Begin class body
		if (!Curvy.open(fork)) {
			return Optional.empty();
		}

		// Explore class body
		List<VariableDeclaration> staticFields = new ArrayList<>();
		List<VariableDeclaration> instanceFields = new ArrayList<>();
		List<Constructor> constructors = new ArrayList<>();
		List<MethodDeclaration> staticMethods = new ArrayList<>();
		List<MethodDeclaration> methods = new ArrayList<>();
		List<ClassDeclaration> innerClasses = new ArrayList<>();
		List<ClassDeclaration> staticInnerClasses = new ArrayList<>();
		while (!Curvy.close(fork)) {

			// Try to find a constructor
			Optional<Constructor> optionalConstructor = buildConstructor(fork, className.get().getContent());
			if (optionalConstructor.isPresent()) {
				Constructor constructor = optionalConstructor.get();
				constructors.add(constructor);
				continue;
			}

			// Try to find a method
			Optional<MethodDeclaration> optionalMethod = MethodDeclaration.build(fork);
			if (optionalMethod.isPresent()) {
				MethodDeclaration method = optionalMethod.get();
				if (method.getQualifierEnums().contains(JavaQualifier.STATIC)) {
					staticMethods.add(method);
				} else {
					methods.add(method);
				}
				continue;
			}

			// Try to find a field
			Optional<VariableDeclaration> optionalvar = VariableDeclaration.build(fork);
			if (optionalvar.isPresent()) {
				if (EmptyExpression.build(fork).isPresent()) { // expect ';'
					VariableDeclaration var = optionalvar.get();
					if (var.getQualifierEnums().contains(JavaQualifier.STATIC)) {
						staticFields.add(var);
					} else {
						instanceFields.add(var);
					}
				}
				continue;
			}

			// Try to find an inner class
			Optional<ClassDeclaration> optionalInnerClass = ClassDeclaration.build(fork);
			if (optionalInnerClass.isPresent()) {
				ClassDeclaration innerClass = optionalInnerClass.get();
				if (innerClass.getQualifierEnums().contains(JavaQualifier.STATIC)) {
					staticInnerClasses.add(innerClass);
				} else {
					innerClasses.add(innerClass);
				}
				continue;
			}
		}

		// Success!
		CodeLocation location = fork.commit();
		return Optional.of(new ClassDeclaration(qualifiers, className.get(), typeParameters, extendsClass, implementedInterfaces, staticFields, instanceFields,
			constructors, staticMethods, methods, innerClasses, staticInnerClasses, location));
	}

	/**
	 * Find 'class' keyword, advance the cursor to after it.
	 * 
	 * @param input
	 * @return
	 */
	private static boolean expectClassKeyword(CodeBranch input) {
		Matcher matcher = classKeyword.matcher(input.getRest());
		if (!matcher.lookingAt()) {
			return false;
		}
		input.advance(matcher.end());
		JavaWhitespace.skipWhitespaceAndComments(input);
		return true;
	}

	private static boolean findParameter(CodeBranch input, List<ClassName> found) {
		// Attempt to find a simple parameter name
		Optional<ClassName> typeParameter = ClassName.build(input);
		if (typeParameter.isPresent()) {
			// Found a named parameter!
			found.add(typeParameter.get());
			return true;
		} else {
			return false;
		}
	}

	private static boolean matchSeparator(CodeBranch input) {
		Matcher separatorMatcher = separatorPattern.matcher(input.getRest());
		if (separatorMatcher.lookingAt()) {
			input.advance(separatorMatcher.end());
			return true;
		} else {
			return false;
		}
	}

	private static boolean openChevron(CodeBranch input) {
		Matcher beginMatcher = beginChevronPattern.matcher(input.getRest());
		if (beginMatcher.lookingAt()) {
			input.advance(beginMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(input);
			return true;
		} else {
			return false;
		}
	}

	private static void expectEndChevron(CodeBranch input) {
		Matcher endMatcher = endChevronPattern.matcher(input.getRest());
		if (endMatcher.lookingAt()) {
			input.advance(endMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(input);
		} else {
			throw new RuntimeException("Could not find closing chevrons after Type parameters!");
		}
	}

	private static List<ClassName> findInterfaces(CodeBranch input) {
		Matcher extendsMatcher = implementsPattern.matcher(input.getRest());
		if (extendsMatcher.lookingAt()) {

			// found interfaces class
			input.advance(extendsMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(input);

			// Now list interfaces
			List<ClassName> interfaces = new ArrayList<>();
			while (true) {
				Optional<ClassName> optionalSuper = ClassName.build(input);
				if (optionalSuper.isPresent()) {
					// found one interface!
					interfaces.add(optionalSuper.get());
					if (matchSeparator(input)) {
						continue; // let's find another one!
					} else {
						return interfaces; // it's over...
					}
				} else {
					throw new RuntimeException("Missing class name in interface list");
				}
			}
		}
		return new ArrayList<>();
	}

	/**
	 * Attempts to build this class' constructor.
	 * 
	 * @param inputRef
	 *            the input string (mutated if a constructor is found)
	 * @param surroundingClassName
	 *            the expected class name
	 * @return optionally, a constructor
	 */
	public static Optional<Constructor> buildConstructor(CodeBranch inputRef, String surroundingClassName) {
		CodeBranch defensiveCopy = inputRef.fork();

		// Constructor properties (name, qualifiers, hierarchy)
		List<Qualifiers> constructorQualifiers = Qualifiers.searchQualifiers(defensiveCopy); // TODO some are illegal! (static etc.)

		// Match constructor
		Optional<ShowableString> constructorName = ShowableString.fromPattern(defensiveCopy, Pattern.compile(surroundingClassName));
		if (!constructorName.isPresent()) {
			return Optional.empty();
		}

		// Constructor parameters
		Optional<List<ParameterTypeDeclaration>> parameters = ParameterTypeDeclaration.buildSeries(defensiveCopy);
		if (!parameters.isPresent()) {
			return Optional.empty();
		}

		// Begin Constructor body
		if (!Curvy.open(defensiveCopy)) {
			return Optional.empty();
		}

		// Explore Constructor body
		List<Expression> expressions = new ArrayList<>();
		while (!Curvy.close(defensiveCopy)) {

			// Try to find a statement
			Optional<? extends Expression> optionalExpression = Expression.build(defensiveCopy);
			if (optionalExpression.isPresent()) {
				Expression expression = optionalExpression.get();
				expressions.add(expression);
			}

		}

		// System.out.println("Found constructor for class " + surroundingClassName);
		return Optional.of(new Constructor(constructorName.get(), constructorQualifiers, parameters.get(), expressions, defensiveCopy.commit()));
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
		String qualiOffset = new String(new char[qualiWidth]).replace("\0", " ");

		// Class name
		result.add(prefix + qualiOffset + " class " + className);
		int nameWidth = className.getContent().length();
		String nameOffset = new String(new char[nameWidth + 7]).replace("\0", " ");

		// extends
		if (extendsClass != null && extendsClass.isPresent()) {
			String keyword = " extends ";
			for (String line : extendsClass.get().fullBreakdown("")) {
				result.add(prefix + qualiOffset + nameOffset + keyword + line);
				keyword = "         ";
			}
		}
		// Interfaces
		if (!implementedInterfaces.isEmpty()) {
			String keyword = " implements ";
			Iterator<ClassName> interfaceIterator = implementedInterfaces.iterator();
			while (interfaceIterator.hasNext()) {
				result.add(prefix + qualiOffset + nameOffset + keyword + interfaceIterator.next() + (interfaceIterator.hasNext() ? "," : ""));
				keyword = "            ";
			}
		}
		result.add(prefix + qualiOffset + nameOffset + " {");

		// Body
		String bodyPrefix = prefix + "c  ";
		for (ClassDeclaration staticInnerClass : staticInnerClasses) {
			result.addAll(staticInnerClass.fullBreakdown(bodyPrefix));
		}
		for (ClassDeclaration innerClass : innerClasses) {
			result.addAll(innerClass.fullBreakdown(bodyPrefix));
		}
		for (VariableDeclaration staticField : staticFields) {
			result.addAll(staticField.fullBreakdown(bodyPrefix));
		}
		for (VariableDeclaration field : fields) {
			result.addAll(field.fullBreakdown(bodyPrefix));
		}
		for (MethodDeclaration staticMethod : staticMethods) {
			result.addAll(staticMethod.fullBreakdown(bodyPrefix));
		}
		for (MethodDeclaration method : methods) {
			result.addAll(method.fullBreakdown(bodyPrefix));
		}

		// Footer
		result.add(prefix + "}");

		return result;
	}

	@Override
	public List<String> nativeFormat(String prefix) {
		// Header
		StringBuilder header = new StringBuilder(prefix);
		header.append(Qualifiers.toString(qualifiers)).append(" class ").append(className);

		// extends
		if (extendsClass != null && extendsClass.isPresent()) {
			header.append(" extends ").append(extendsClass.get().toString());
		}

		// Interfaces
		if (!implementedInterfaces.isEmpty()) {
			header.append(" implements ");
			header.append(String.join(", ", implementedInterfaces.stream().flatMap(itf -> itf.nativeFormat("").stream()).collect(Collectors.toList())));

		}
		header.append(" {");

		// Body
		List<String> body = new ArrayList<>();
		String bodyPrefix = prefix + "c  ";
		for (ClassDeclaration staticInnerClass : staticInnerClasses) {
			body.addAll(staticInnerClass.nativeFormat(bodyPrefix));
		}
		for (ClassDeclaration innerClass : innerClasses) {
			body.addAll(innerClass.nativeFormat(bodyPrefix));
		}
		for (VariableDeclaration staticField : staticFields) {
			body.addAll(staticField.nativeFormat(bodyPrefix));
		}
		for (VariableDeclaration field : fields) {
			body.addAll(field.nativeFormat(bodyPrefix));
		}
		for (MethodDeclaration staticMethod : staticMethods) {
			body.addAll(staticMethod.nativeFormat(bodyPrefix));
		}
		for (MethodDeclaration method : methods) {
			body.addAll(method.nativeFormat(bodyPrefix));
		}
		// Footer
		String footer = prefix + "}";

		// Concat it all
		List<String> total = new ArrayList<>();
		total.add(header.toString());
		total.addAll(body);
		total.add(footer);
		return total;
	}

	@Override
	public <T> T acceptSyntaxVisitor(JavaSyntaxVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
