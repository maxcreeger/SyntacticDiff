package lexeme.java.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import diff.similarity.SimpleSimilarity.ShowableString;
import lexer.Structure;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a class name, like {@code  List<String>[]}<br>
 * May contain class parameters in chevrons "<>" and dimensions
 */
@AllArgsConstructor
@Getter
public class ClassName implements JavaSyntax, Structure<JavaGrammar> {

	public static final Pattern primitiveTypeClassNames = Pattern.compile("(byte)|(short)|(int)|(long)|(float)|(double)|(char)|(boolean)");
	private static final Pattern superPattern = Pattern.compile("super");

	private static final Pattern beginChevronPattern = Pattern.compile("<");
	private static final Pattern classNamePattern = Pattern.compile("\\w+");
	private static final Pattern beginArrayPattern = Pattern.compile("\\[");
	private static final Pattern closeArrayPattern = Pattern.compile("\\]");
	private static final Pattern separatorPattern = Pattern.compile("\\s*,\\s*");
	private static final Pattern endChevronPattern = Pattern.compile(">");

	private final ShowableString name;
	private final Optional<ClassName> superClass; // used for Bounding Generics Type parameters TODO must account '?' wildcard etc.
	private final List<ClassName> nestedSubParameters; // Null for N/A, empty for Type inference
	private final Optional<ArrayDimension> arrayDimension;
	private final CodeLocation location;

	@Getter
	@AllArgsConstructor
	public static class ArrayDimension implements Showable {

		int dimensions;
		CodeLocation location;

		@Override
		public List<String> fullBreakdown(String prefix) {
			List<String> list = new ArrayList<>();
			StringBuilder val = new StringBuilder();
			for (int dim = 0; dim < dimensions; dim++) {
				val.append("[]");
			}
			list.add(prefix + val.toString());
			return list;
		}
	}

	/**
	 * Attempts to build a class name.
	 * 
	 * @param input
	 *            the mutable input String (is modified if a class name is
	 *            found)
	 * @return optionally, a class name
	 */
	public static Optional<ClassName> build(CodeBranch input) {
		CodeBranch fork = input.fork();

		// Class name
		Optional<ShowableString> className = ShowableString.fromPattern(fork, classNamePattern);
		if (!className.isPresent()) {
			return Optional.empty();
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

		// Attempt to find super for bounding generics
		Optional<ClassName> superClass = find(fork, superPattern);

		// Attempt to find a n-dimensional array
		Optional<ArrayDimension> arrayDimension = countArrayDimension(fork);

		return Optional.of(new ClassName(className.get(), superClass, typeParameters, arrayDimension, fork.commit()));
	}

	private static Optional<ArrayDimension> countArrayDimension(CodeBranch input) {
		CodeBranch fork = input.fork();
		boolean foundOne = false;
		int dimensions = 0;
		do {
			foundOne = false;
			CodeBranch reFork = fork.fork();
			// Open
			Matcher beginArrayMatcher = beginArrayPattern.matcher(reFork.getRest());
			if (!beginArrayMatcher.lookingAt()) {
				break;
			}
			reFork.advance(beginArrayMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(reFork);

			// Close
			Matcher closeArrayMatcher = closeArrayPattern.matcher(reFork.getRest());
			if (!closeArrayMatcher.lookingAt()) {
				return Optional.empty();
			}
			reFork.advance(closeArrayMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(reFork);

			// collect
			foundOne = true;
			dimensions++;
			reFork.commit();
		} while (foundOne);

		if (dimensions > 0) {
			return Optional.of(new ArrayDimension(dimensions, fork.commit()));
		} else {
			return Optional.empty();
		}
	}

	public static Optional<ClassName> find(CodeBranch input, Pattern pattern) {
		Matcher extendsMatcher = pattern.matcher(input.getRest());
		if (extendsMatcher.lookingAt()) {
			// found super class
			input.advance(extendsMatcher.end());
			JavaWhitespace.skipWhitespaceAndComments(input);
			// Get super ClassName
			Optional<ClassName> optionalSuper = ClassName.build(input);
			if (optionalSuper.isPresent()) {
				return optionalSuper;
			} else {
				throw new RuntimeException("No class name found after " + pattern.toString() + " keyword in class definition");
			}
		}
		return Optional.empty();
	}

	private static boolean findParameter(CodeBranch input, List<ClassName> found) {
		// Attempt to find a simple parameter name
		Optional<ClassName> typeParameter = build(input);
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

	/**
	 * Compare a list of class names to another list (order does matter!)
	 * 
	 * @param classNames1
	 *            a list of class names
	 * @param classNames2
	 *            a list of class names
	 * @return true if they are the same
	 */
	public static boolean compare(List<ClassName> classNames1, List<ClassName> classNames2) {
		if (classNames1.size() != classNames2.size()) {
			return false;
		}
		for (int num = 0; num < classNames1.size(); num++) {
			ClassName nameA = classNames1.get(num);
			ClassName nameB = classNames2.get(num);
			if (!nameA.compare(nameB)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares this class name to another
	 * 
	 * @param other
	 *            the other class name
	 * @return true if they are the same
	 */
	public boolean compare(ClassName other) {
		if (!name.equals(other.name)) {
			return false;
		}
		if (arrayDimension != other.arrayDimension) {
			return false;
		}
		if (nestedSubParameters == null) {
			if (other.nestedSubParameters != null) {
				return false;
			}
		} else {
			if (other.nestedSubParameters == null || nestedSubParameters.size() != other.nestedSubParameters.size()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public <T> T acceptSyntaxVisitor(JavaSyntaxVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return nativeFormat("").get(0).toString();
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		// TODO
		List<String> result = new ArrayList<>();
		result.add(prefix + getName());
		return result;
	}

	/**
	 * Guaranteed 1-liner
	 */
	@Override
	public List<String> nativeFormat(String prefix) {
		//TODO better
		StringBuilder builder = new StringBuilder();

		// Class name
		builder.append(name);

		// superclass
		if (superClass != null && superClass.isPresent()) {
			builder.append(" super ").append(superClass.get().fullBreakdown(prefix));
		}

		// Class parameters
		if (nestedSubParameters != null && !nestedSubParameters.isEmpty()) {
			builder.append("<");
			Iterator<ClassName> paramIterator = nestedSubParameters.iterator();
			while (paramIterator.hasNext()) {
				builder.append(paramIterator.next());
				if (paramIterator.hasNext()) {
					builder.append(", ");
				}
			}
			builder.append(">");
		}

		// Arrays
		if (getArrayDimension().isPresent()) {
			for (int lvl = 0; lvl < arrayDimension.get().getDimensions(); lvl++) {
				builder.append("[]");
			}
		}
		return Arrays.asList(builder.toString()); // Guaranteed 1-liner
	}

}
