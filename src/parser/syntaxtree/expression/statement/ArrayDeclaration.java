package parser.syntaxtree.expression.statement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diff.complexity.Showable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.ClassName;
import parser.syntaxtree.Whitespace;
import parser.syntaxtree.expression.Expression;
import parser.tokens.Bracket;
import parser.tokens.Curvy;

/**
 * The creation of an array. May be initialized with values or left default.<br>
 * 
 * <code>new int[][] {{2}, {1, 5}};</code><br>
 * <code>new int[2][5];</code>
 */
@Getter
@AllArgsConstructor
public class ArrayDeclaration extends Statement {

    private static final Pattern newPattern = Pattern.compile("new\\s+");
    private static final Pattern separator = Pattern.compile(",");

    private final ClassName className;
    private final ArrayInitialization init;


    public static Optional<ArrayDeclaration> build(AtomicReference<String> input) {
        Matcher newMatcher = newPattern.matcher(input.get());
        if (!newMatcher.lookingAt()) {
            return Optional.empty();
        }

        // Reserved keyword 'new' has been found
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(input.get().substring(newMatcher.end()));
        Whitespace.skipWhitespaceAndComments(defensiveCopy);

        Optional<ClassName> className = ClassName.build(input);
        if (!className.isPresent()) {
            return Optional.empty();
        }

        if (className.get().getArrayDimension() > 0) {
            // Array initialization with content: new int[][] {{2}, {1, 5}};
            Optional<ArrayInitialization> init = discoverInitializationWithContent(input);
            return Optional.of(new ArrayDeclaration(className.get(), init.get()));
        } else {
            // Empty Array initialization : new int[2][5];
            Optional<ArraySizeDeclaration> init = discoverEmptyInitialization(input);
            return Optional.of(new ArrayDeclaration(className.get(), init.get()));

        }
    }


    /**
     * Discover an empty array initialization :<br>
     * <code>new int[nx][ny];</code>
     * 
     * @param input the input mutable string
     * @return optionally, an {@link ArrayInitialization}
     */
    private static Optional<ArraySizeDeclaration> discoverEmptyInitialization(AtomicReference<String> inputRef) {
        List<Expression> arraySizes = new ArrayList<>();
        boolean keepOn = true;
        AtomicReference<String> input = new AtomicReference<String>(inputRef.get());
        while (keepOn) {
            boolean begin = Curvy.open(input);
            if (!begin) {
                break;
            }
            Optional<? extends Expression> dimensionExpression = Expression.build(input);
            if (!dimensionExpression.isPresent()) {
                break;
            }
            boolean end = Bracket.close(input);
            if (!end) {
                break;
            }
            arraySizes.add(dimensionExpression.get());
        }
        if (arraySizes.isEmpty()) {
            return Optional.empty();
        } else {
            // Commit
            inputRef.set(input.get());
            return Optional.of(new ArraySizeDeclaration(arraySizes.toArray(new Expression[arraySizes.size()])));
        }
    }

    /**
     * Discover array initialization with brackets:<br>
     * <code>new int[][] {{2}, {1, 5}};</code>
     * 
     * @param input the input mutable string
     * @return optionally, an {@link ArrayInitialization}
     */
    private static Optional<ArrayInitialization> discoverInitializationWithContent(AtomicReference<String> inputRef) {
        AtomicReference<String> input = new AtomicReference<String>(inputRef.get());
        int nbDim = discoverArrayDimension(input);
        if (nbDim <= 0) {
            return Optional.empty();
        } else {
            Optional<ArrayInitialization> result = discoverContentInitialization(input, nbDim);
            inputRef.set(input.get());
            return result;
        }
    }

    /**
     * Discover array dimension with square brackets:<br>
     * <code>[][]</code>
     * 
     * @param input the input mutable string
     * @return the number of square bracket pairs
     */
    private static int discoverArrayDimension(AtomicReference<String> inputRef) {
        // Match empty square bracket chain "[][]"
        int nbDimensions = 0;
        boolean keepOn = true;
        AtomicReference<String> input = new AtomicReference<String>(inputRef.get());
        while (keepOn) {
            boolean begin = Bracket.open(input);
            if (!begin) {
                break;
            }
            Optional<? extends Expression> dimensionExpression = Expression.build(input);
            if (!dimensionExpression.isPresent()) {
                break;
            }
            boolean end = Bracket.close(input);
            if (!end) {
                break;
            }
            nbDimensions++;
        }
        if (nbDimensions > 0) {
            // Commit
            inputRef.set(input.get());
            return nbDimensions;
        } else {
            return -1; // Failed
        }
    }

    /**
     * Discover array initialization with brackets:<br>
     * <code>new int[][] {{2}, {1, 5}};</code>
     * 
     * @param input the input mutable string
     * @return optionally, an {@link ArrayInitialization}
     */
    private static Optional<ArrayInitialization> discoverContentInitialization(AtomicReference<String> inputRef, int expectedDimensions) {
        if (expectedDimensions == 0) {
            // Attempt a series of leaf statements separated by ','
            Optional<? extends Statement> leaf;
            List<Statement> found = new ArrayList<>();
            do {
                leaf = Statement.build(inputRef);
                if (leaf.isPresent()) {
                    found.add(leaf.get());
                } else {
                    break;
                }
            } while (separator(inputRef));
            return Optional.of(new ArrayInitialisationLeaf(found));
        } else {
            // Expect '{'
            if (!Curvy.open(inputRef)) {
                return Optional.empty();
            }
            // Attempt further dimensions (repeatedly) inside this, like '{5}, {8}'
            Optional<ArrayInitialization> val = discoverContentInitialization(inputRef, expectedDimensions - 1);
            if (!val.isPresent()) {
                return Optional.empty();
            }
            List<ArrayInitialization> found = new ArrayList<>();
            found.add(val.get());
            while (separator(inputRef)) {
                val = discoverContentInitialization(inputRef, expectedDimensions - 1);
                if (val.isPresent()) {
                    found.add(val.get());
                } else {
                    break;
                }
            }
            // Expect '}'
            if (!Curvy.close(inputRef)) {
                return Optional.empty();
            } else {
                return Optional.of(new ArrayInitialisationRec(found));
            }
        }
    }

    /**
     * Tree class for array dimension for initialization (could be intermediate or 'leaf' dimensions).
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public abstract static class ArrayInitialization implements Showable {

        /**
         * Accepts an {@link ArrayInitializationVisitor} to to something on it.
         * @param <T> the return type of the visitor
         * @param visitor the visitor
         * @return the outcome of the visit
         */
        public abstract <T> T accept(ArrayInitializationVisitor<T> visitor);

    }

    /**
     * Visitor of an array initialization.
     *
     * @param <T> return type of the visit
     */
    public interface ArrayInitializationVisitor<T> {

        /**
         * Visit a 'leaf' initialization (terminal)
         * 
         * @param leaf the leaf to visit
         * @return the output
         */
        T visit(ArrayInitialisationLeaf leaf);


        /**
         * Visit a 'Node' initialization (non-terminal, recursive).
         * 
         * @param rec the initialization level to visit
         * @return the output
         */
        T visit(ArrayInitialisationRec rec);


        /**
         * Visit an array size declaration.
         * 
         * @param decla the size declaration
         * @return the output
         */
        T visit(ArraySizeDeclaration decla);

    }

    /**
     * Declares the total size of a multi-dimensional array like <code>[5][5]</code>.
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ArraySizeDeclaration extends ArrayInitialization {

        private final Expression[] dimensions;

        public List<String> show(String prefix) {
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

        public List<String> show(String prefix) {
            List<String> builder = new ArrayList<>();
            builder.add(prefix + "{");
            Iterator<ArrayInitialization> iter = subDimensions.iterator();
            while (iter.hasNext()) {
                ArrayInitialization sub = iter.next();
                builder.addAll(sub.show(prefix + "   "));
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
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ArrayInitialisationLeaf extends ArrayInitialization {

        List<Statement> values;

        public List<String> show(String prefix) {
            List<String> result = new ArrayList<>();
            result.add(prefix + "{");
            Iterator<Statement> iter = values.iterator();
            while (iter.hasNext()) {
                Statement statement = iter.next();
                result.addAll(statement.show(prefix + "   " + (iter.hasNext() ? "," : "")));
            }
            result.add(prefix + '}');
            return result;
        }

        @Override
        public <T> T accept(ArrayInitializationVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    private static boolean separator(AtomicReference<String> input) {
        Matcher matcher = separator.matcher(input.get());
        if (matcher.lookingAt()) {
            input.set(input.get().substring(1));
            Whitespace.skipWhitespaceAndComments(input);
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
    public List<String> show(String prefix) {
        List<String> result = new ArrayList<>();
        result.add(prefix + "new " + className.toString());
        result.addAll(init.show(prefix + "i  "));
        return result;
    }

}
