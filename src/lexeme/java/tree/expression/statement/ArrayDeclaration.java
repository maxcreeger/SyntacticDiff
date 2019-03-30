package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lexeme.java.intervals.Bracket;
import lexeme.java.intervals.Curvy;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.Expression;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * The creation of an array. May be initialized with values or left default.<br>
 * 
 * <code>new int[][] {{2}, {1, 5}};</code><br>
 * <code>new int[2][5];</code>
 */
@Getter
public class ArrayDeclaration extends Statement {

	private static final Pattern separator = Pattern.compile(",");

	private final ArrayInitialization init;

	public ArrayDeclaration(ArrayInitialization init, CodeLocation location) {
		super(location);
		this.init = init;
	}

	public static Optional<ArrayDeclaration> build(CodeBranch input) {
		CodeBranch fork = input.fork();
		Optional<ArrayInitialization> init;
		// Array initialization with content: new int[][] {{2}, {1, 5}};
		init = discoverInitializationWithContent(fork);
		if (init.isPresent()) {
			return Optional.of(new ArrayDeclaration(init.get(), fork.commit()));
		}
		// Empty Array initialization : new int[2][5];
		init = discoverEmptyInitialization(fork);
		if (init.isPresent()) {
			return Optional.of(new ArrayDeclaration(init.get(), fork.commit()));
		}
		return Optional.empty();
	}

	/**
	 * Discover an empty array initialization :<br>
	 * <code>new int[nx][ny];</code>
	 * 
	 * @param input
	 *            the input mutable string
	 * @return optionally, an {@link ArrayInitialization}
	 */
	private static Optional<ArrayInitialization> discoverEmptyInitialization(CodeBranch inputRef) {
		List<Expression> arraySizes = new ArrayList<>();
		CodeBranch fork = inputRef.fork();
		while (true) {
			boolean begin = Bracket.open(fork);
			if (!begin) {
				break;
			}
			Optional<? extends Expression> dimensionExpression = Statement.build(fork);
			if (!dimensionExpression.isPresent()) {
				break;
			}
			boolean end = Bracket.close(fork);
			if (!end) {
				break;
			}
			arraySizes.add(dimensionExpression.get());
		}
		if (arraySizes.isEmpty()) {
			return Optional.empty();
		} else {
			// Commit
			return Optional.of(new ArraySizeDeclaration(arraySizes.toArray(new Expression[arraySizes.size()]), fork.commit()));
		}
	}

	/**
	 * Discover array initialization with brackets:<br>
	 * <code>new int[][] {{2}, {1, 5}};</code>
	 * 
	 * @param input
	 *            the input mutable string
	 * @return optionally, an {@link ArrayInitialization}
	 */
	private static Optional<ArrayInitialization> discoverInitializationWithContent(CodeBranch inputRef) {
		int nbDim = discoverArrayDimension(inputRef);
		if (nbDim <= 0) {
			return Optional.empty();
		} else {
			Optional<ArrayInitialization> result = discoverContentInitialization(inputRef, nbDim);
			return result;
		}
	}

	/**
	 * Discover array dimension with square brackets:<br>
	 * <code>[][]</code>
	 * 
	 * @param input
	 *            the input mutable string
	 * @return the number of square bracket pairs
	 */
	private static int discoverArrayDimension(CodeBranch input) {
		CodeBranch fork = input.fork();
		// Match empty square bracket chain "[][]"
		int nbDimensions = 0;
		while (true) {
			boolean begin = Bracket.open(fork);
			if (!begin) {
				break;
			}
			Optional<? extends Expression> dimensionExpression = Expression.build(fork);
			if (!dimensionExpression.isPresent()) {
				break;
			}
			boolean end = Bracket.close(fork);
			if (!end) {
				break;
			}
			nbDimensions++;
		}
		if (nbDimensions > 0) {
			// Commit
			fork.commit();
			return nbDimensions;
		} else {
			return -1; // Failed
		}
	}

	/**
	 * Discover array initialization with brackets:<br>
	 * <code>new int[][] {{2}, {1, 5}};</code>
	 * 
	 * @param input
	 *            the input mutable string
	 * @return optionally, an {@link ArrayInitialization}
	 */
	private static Optional<ArrayInitialization> discoverContentInitialization(CodeBranch inputRef, int expectedDimensions) {
		if (expectedDimensions == 0) {
			// Attempt a series of leaf statements separated by ','
			CodeBranch fork = inputRef.fork();
			Optional<? extends Statement> leaf;
			List<Statement> found = new ArrayList<>();
			do {
				leaf = Statement.build(fork);
				if (leaf.isPresent()) {
					found.add(leaf.get());
				} else {
					break;
				}
			} while (separator(fork));
			return Optional.of(new ArrayInitialisationLeaf(found, fork.commit()));
		} else {
			CodeBranch fork = inputRef.fork();
			// Expect '{'
			if (!Curvy.open(fork)) {
				return Optional.empty();
			}
			// Attempt further dimensions (repeatedly) inside this, like '{5}, {8}'
			Optional<ArrayInitialization> val = discoverContentInitialization(fork, expectedDimensions - 1);
			if (!val.isPresent()) {
				return Optional.empty();
			}
			List<ArrayInitialization> found = new ArrayList<>();
			found.add(val.get());
			while (separator(fork)) {
				val = discoverContentInitialization(fork, expectedDimensions - 1);
				if (val.isPresent()) {
					found.add(val.get());
				} else {
					break;
				}
			}
			// Expect '}'
			if (!Curvy.close(fork)) {
				return Optional.empty();
			} else {
				return Optional.of(new ArrayInitialisationRec(found, fork.commit()));
			}
		}
	}

	/**
	 * Tree class for array dimension for initialization (could be intermediate
	 * or 'leaf' dimensions).
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public abstract static class ArrayInitialization implements Showable {

		/**
		 * Accepts an {@link ArrayInitializationVisitor} to to something on it.
		 * 
		 * @param <T>
		 *            the return type of the visitor
		 * @param visitor
		 *            the visitor
		 * @return the outcome of the visit
		 */
		public abstract <T> T accept(ArrayInitializationVisitor<T> visitor);

	}

	/**
	 * Visitor of an array initialization.
	 *
	 * @param <T>
	 *            return type of the visit
	 */
	public interface ArrayInitializationVisitor<T> {

		/**
		 * Visit a 'leaf' initialization (terminal)
		 * 
		 * @param leaf
		 *            the leaf to visit
		 * @return the output
		 */
		T visit(ArrayInitialisationLeaf leaf);

		/**
		 * Visit a 'Node' initialization (non-terminal, recursive).
		 * 
		 * @param rec
		 *            the initialization level to visit
		 * @return the output
		 */
		T visit(ArrayInitialisationRec rec);

		/**
		 * Visit an array size declaration.
		 * 
		 * @param decla
		 *            the size declaration
		 * @return the output
		 */
		T visit(ArraySizeDeclaration decla);

	}

	/**
	 * Declares the total size of a multi-dimensional array like
	 * <code>[5][5]</code>.
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class ArraySizeDeclaration extends ArrayInitialization {

		private final Expression[] dimensions;
		private final CodeLocation location;

		@Override
		public List<String> fullBreakdown(String prefix) {
			List<String> result = new ArrayList<>();
			StringBuilder dim = new StringBuilder(prefix);
			for (Expression i : dimensions) {
				dim.append("[").append(i).append("]");
			}
			result.add(dim.toString());
			return result;
		}

		@Override
		public <T> T accept(ArrayInitializationVisitor<T> visitor) {
			return visitor.visit(this);
		}
	}

	/**
	 * A dimension along which the array is initialized.
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class ArrayInitialisationRec extends ArrayInitialization {

		List<ArrayInitialization> subDimensions;
		private final CodeLocation location;

		@Override
		public List<String> fullBreakdown(String prefix) {
			List<String> builder = new ArrayList<>();
			builder.add(prefix + "{");
			Iterator<ArrayInitialization> iter = subDimensions.iterator();
			while (iter.hasNext()) {
				ArrayInitialization sub = iter.next();
				builder.addAll(sub.fullBreakdown(prefix + "   "));
			}
			builder.add(prefix + "}");
			return builder;
		}

		@Override
		public <T> T accept(ArrayInitializationVisitor<T> visitor) {
			return visitor.visit(this);
		}

	}

	/**
	 * Initialization of an 'leaf' Array dimension with actual Statements.
	 */
	@Getter
	public static class ArrayInitialisationLeaf extends ArrayInitialization {
		List<Statement> values;
		CodeLocation location;

		protected ArrayInitialisationLeaf(List<Statement> values, CodeLocation location) {
			super();
			this.values = values;
			this.location = location;
		}

		@Override
		public List<String> fullBreakdown(String prefix) {
			List<String> result = new ArrayList<>();
			result.add(prefix + "{");
			Iterator<Statement> iter = values.iterator();
			while (iter.hasNext()) {
				Statement statement = iter.next();
				result.addAll(statement.fullBreakdown(prefix + "   " + (iter.hasNext() ? "," : "")));
			}
			result.add(prefix + '}');
			return result;
		}

		@Override
		public <T> T accept(ArrayInitializationVisitor<T> visitor) {
			return visitor.visit(this);
		}
	}

	private static boolean separator(CodeBranch input) {
		Matcher matcher = separator.matcher(input.getRest());
		if (matcher.lookingAt()) {
			input.advance(1);
			JavaWhitespace.skipWhitespaceAndComments(input);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isAssignable() {
		return false;
	}

	@Override
	public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	// display

	@Override
	public List<String> fullBreakdown(String prefix) {
		List<String> result = new ArrayList<>();
		result.addAll(init.fullBreakdown(prefix));
		return result;
	}

}
