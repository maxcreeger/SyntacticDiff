package lexeme.java.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexer.Structure;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a class name.<br>
 * May contain class parameters in chevrons "<>", and may reference interfaces and extensions
 */
@AllArgsConstructor
@Getter
public class ClassName implements JavaSyntax, Structure<JavaGrammar> {

    /**
     * Primitive type Matcher
     */
    public static final Pattern primitiveTypeClassNames = Pattern.compile("(byte)|(short)|(int)|(long)|(float)|(double)|(char)|(boolean)");

    private static final Pattern beginChevronPattern = Pattern.compile("<");
    private static final Pattern classNamePattern = Pattern.compile("\\w+");
    private static final Pattern arrayPattern = Pattern.compile("(\\[\\s*\\])"); // an 1-dimensional array type
    private static final Pattern extendsPattern = Pattern.compile("extends");
    private static final Pattern implementsPattern = Pattern.compile("implements");
    private static final Pattern superPattern = Pattern.compile("super");
    private static final Pattern separatorPattern = Pattern.compile("\\s*,\\s*");
    private static final Pattern endChevronPattern = Pattern.compile(">");

    private final String name;
    private final List<ClassName> nestedSubParameters; // Null for N/A, empty for Type inference
    /** The number of dimensions. 0 for just a value without an array */
    private final int arrayDimension;
    private final Optional<ClassName> superClass;
    private final Optional<ClassName> extendsClass;
    private final List<ClassName> implementedInterfaces;
    private final CodeLocation location;

    /**
     * Attempts to build a class name.
     * @param input the mutable input String (is modified if a class name is found)
     * @return optionally, a class name
     */
    public static Optional<ClassName> build(CodeBranch input) {
        CodeBranch local = input.fork();
        Matcher classNameMatcher = classNamePattern.matcher(local.getRest());
        if (!classNameMatcher.lookingAt()) {
            // Not a class name!
            return Optional.empty();
        }
        String className = classNameMatcher.group(0);
        // Advancing input to end of class name
        local.advance(className.length());
        JavaWhitespace.skipWhitespaceAndComments(local);

        // Attempt to find some Type parameters
        List<ClassName> found = null;
        if (openChevron(local)) {
            found = new ArrayList<>(); // Non null!
            while (findParameter(local, found)) {
                // Attempt to find a separator between parameters
                matchSeparator(local);
            }
            expectEndChevron(local);
        }

        // Attempt to find super
        Optional<ClassName> superClass = find(local, superPattern);

        // Attempt to find extends
        Optional<ClassName> extendsClass = find(local, extendsPattern);

        // Attempt to find implements
        List<ClassName> implementedInterfaces = findInterfaces(local);

        // Attempt to find a n-dimensional array
        int arrayDimension = countArrayDimension(local);

        return Optional.of(new ClassName(className, found, arrayDimension, superClass, extendsClass, implementedInterfaces, local.commit()));
    }

    private static Optional<ClassName> find(CodeBranch input, Pattern pattern) {
        Matcher extendsMatcher = pattern.matcher(input.getRest());
        if (extendsMatcher.lookingAt()) {
            // found super class
            input.advance(extendsMatcher.end());
            JavaWhitespace.skipWhitespaceAndComments(input);
            // Get super ClassName
            Optional<ClassName> optionalSuper = build(input);
            if (optionalSuper.isPresent()) {
                return optionalSuper;
            } else {
                throw new RuntimeException("No class name found after " + pattern.toString() + " keyword in class definition");
            }
        }
        return Optional.empty();
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
                Optional<ClassName> optionalSuper = build(input);
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

    private static int countArrayDimension(CodeBranch input) {
        int arrayDimension = 0; // 0 by default
        while (true) {
            Matcher arrayMatcher = arrayPattern.matcher(input.getRest());
            if (!arrayMatcher.lookingAt()) {
                break;
            }
            arrayDimension++;
            input.advance(arrayMatcher.end());
            JavaWhitespace.skipWhitespaceAndComments(input);
        }
        return arrayDimension;
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
     * @param classNames1 a list of class names
     * @param classNames2 a list of class names
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
     * @param other the other class name
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
        StringBuilder builder = new StringBuilder();
        // class name
        builder.append(name);
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
        for (int lvl = 0; lvl < arrayDimension; lvl++) {
            builder.append("[");
        }
        for (int lvl = 0; lvl < arrayDimension; lvl++) {
            builder.append("]");
        }
        // superclass
        if (superClass != null && superClass.isPresent()) {
            builder.append(" super ").append(superClass.toString());
        }
        // extends
        if (extendsClass != null && extendsClass.isPresent()) {
            builder.append(" extends ").append(extendsClass.toString());
        }
        // Interfaces
        if (!implementedInterfaces.isEmpty()) {
            builder.append(" implements ");
            Iterator<ClassName> interfaceIterator = implementedInterfaces.iterator();
            while (interfaceIterator.hasNext()) {
                builder.append(interfaceIterator.next());
                if (interfaceIterator.hasNext()) {
                    builder.append(", ");
                }
            }
        }
        return builder.toString();
    }

    @Override
    public List<String> show(String prefix) {
        List<String> result = new ArrayList<>();
        result.add(prefix + getName());
        return result;
    }

}
