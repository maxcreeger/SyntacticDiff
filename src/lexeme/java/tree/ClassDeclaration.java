package lexeme.java.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tokens.Curvy;
import lexeme.java.tree.expression.EmptyExpression;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.VariableDeclaration;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A complete class declaration, with:
 * <ul>
 * <li>qualifiers</li>
 * <li>static</li>
 * <li>fields</li>
 * <li>instance fields</li>
 * <li>static methods</li>
 * <li>instance methods</li>
 * <li>static inner classes</li>
 * <li>inner classes</li>
 * </ul>
 */
@AllArgsConstructor
@Getter
public class ClassDeclaration implements Syntax {

	private static final Pattern classKeyword = Pattern.compile("class");

	private final List<Qualifiers> qualifiers;
	private final ClassName className;
	private final List<VariableDeclaration> staticFields;
	private final List<VariableDeclaration> fields;
	private final List<Constructor> constructors;
	private final List<MethodDeclaration> staticMethods;
	private final List<MethodDeclaration> methods;
	private final List<ClassDeclaration> innerClasses;
	private final List<ClassDeclaration> staticInnerClasses;

	/**
	 * Attempts to build a {@link ClassDeclaration} from the input.
	 * 
	 * @param inputRef
	 *            a mutable text, if a class definition is found the text
	 *            declaration is removed.
	 * @return optionally, a class declaration
	 */
	public static Optional<ClassDeclaration> build(AtomicReference<String> inputRef) {
		AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get());

		// Class properties (name, qualifiers, hierarchy)
		List<Qualifiers> qualifiers = Qualifiers.searchQualifiers(defensiveCopy);

		// Class keyword
		if (!expectClassKeyword(defensiveCopy)) {
			return Optional.empty();
		}

		// Class name
		Optional<ClassName> className = ClassName.build(defensiveCopy);
		if (!className.isPresent()) {
			throw new RuntimeException("Missing class name in class definition");
		}

		// Begin class body
		if (!Curvy.open(defensiveCopy)) {
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
		while (!Curvy.close(defensiveCopy)) {

			// Try to find a constructor
			Optional<Constructor> optionalConstructor = buildConstructor(defensiveCopy, className.get().getName());
			if (optionalConstructor.isPresent()) {
				Constructor constructor = optionalConstructor.get();
				constructors.add(constructor);
				continue;
			}

			// Try to find a method
			Optional<MethodDeclaration> optionalMethod = MethodDeclaration.build(defensiveCopy);
			if (optionalMethod.isPresent()) {
				MethodDeclaration method = optionalMethod.get();
				if (method.getQualifiers().contains(Qualifiers.STATIC)) {
					staticMethods.add(method);
				} else {
					methods.add(method);
				}
				continue;
			}

			// Try to find a field
			Optional<VariableDeclaration> optionalvar = VariableDeclaration.build(defensiveCopy);
			if (optionalvar.isPresent()) {
				if (EmptyExpression.build(defensiveCopy).isPresent()) { // expect ';'
					VariableDeclaration var = optionalvar.get();
					if (var.getQualifiers().contains(Qualifiers.STATIC)) {
						staticFields.add(var);
					} else {
						instanceFields.add(var);
					}
				}
				continue;
			}

			// Try to find an inner class
			Optional<ClassDeclaration> optionalInnerClass = ClassDeclaration.build(defensiveCopy);
			if (optionalInnerClass.isPresent()) {
				ClassDeclaration innerClass = optionalInnerClass.get();
				if (innerClass.getQualifiers().contains(Qualifiers.STATIC)) {
					staticInnerClasses.add(innerClass);
				} else {
					innerClasses.add(innerClass);
				}
				continue;
			}
		}
		return Optional.of(new ClassDeclaration(qualifiers, className.get(), staticFields, instanceFields, constructors, staticMethods, methods, innerClasses,
			staticInnerClasses));
	}

	private static boolean expectClassKeyword(AtomicReference<String> input) {
		Matcher matcher = classKeyword.matcher(input.get());
		if (!matcher.lookingAt()) {
			return false;
		}
		input.set(input.get().substring(matcher.end()));
		JavaWhitespace.skipWhitespaceAndComments(input);
		return true;
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
	public static Optional<Constructor> buildConstructor(AtomicReference<String> inputRef, String surroundingClassName) {
		AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get());

		// Constructor properties (name, qualifiers, hierarchy)
		List<Qualifiers> constructorQualifiers = Qualifiers.searchQualifiers(defensiveCopy); // TODO some are illegal! (static etc.)

		// Match constructor
		Matcher constructorMatcher = Pattern.compile(surroundingClassName).matcher(defensiveCopy.get());
		if (!constructorMatcher.lookingAt()) {
			return Optional.empty();
		}
		defensiveCopy.set(defensiveCopy.get().substring(constructorMatcher.end()));

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

		inputRef.set(defensiveCopy.get());
		// System.out.println("Found constructor for class " + surroundingClassName);
		return Optional.of(new Constructor(surroundingClassName, constructorQualifiers, parameters.get(), expressions));
	}

	@Override
	public String toString() {
		return String.join("\n", show(""));
	}

	@Override
	public List<String> show(String prefix) {
		// Header
		StringBuilder header = new StringBuilder(prefix);
		header.append(Qualifiers.toString(qualifiers)).append(" class ").append(className).append(" {");
		// Body
		List<String> body = new ArrayList<>();
		String bodyPrefix = prefix + "c  ";
		for (ClassDeclaration staticInnerClass : staticInnerClasses) {
			body.addAll(staticInnerClass.show(bodyPrefix));
		}
		for (ClassDeclaration innerClass : innerClasses) {
			body.addAll(innerClass.show(bodyPrefix));
		}
		for (VariableDeclaration staticField : staticFields) {
			body.addAll(staticField.show(bodyPrefix));
		}
		for (VariableDeclaration field : fields) {
			body.addAll(field.show(bodyPrefix));
		}
		for (MethodDeclaration staticMethod : staticMethods) {
			body.addAll(staticMethod.show(bodyPrefix));
		}
		for (MethodDeclaration method : methods) {
			body.addAll(method.show(bodyPrefix));
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
	public <T> T acceptSyntaxVisitor(SyntaxVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
