package lexeme.java.tree.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.ClassName;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.Qualifiers;
import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.VariableReference;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A Variable (or field) declaration
 */
@AllArgsConstructor
@Getter
public class VariableDeclaration extends Expression {

    private static final Pattern assignmentPattern = Pattern.compile("=");

    private final List<Qualifiers> qualifiers;
    private final ClassName type;
    private final String name;
    private final Optional<? extends Statement> initialAssignement;

    public static Optional<VariableDeclaration> build(AtomicReference<String> inputRef) {
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get());

        // Search qualifiers
        List<Qualifiers> qualifiers = Qualifiers.searchQualifiers(defensiveCopy);

        // Search type
        Optional<ClassName> optionalClassName = ClassName.build(defensiveCopy);
        if (!optionalClassName.isPresent()) {
            return Optional.empty();
        }
        ClassName className = optionalClassName.get();

        // Search variableName
        Optional<VariableReference> varName = VariableReference.build(defensiveCopy);
        if (!varName.isPresent()) {
            return Optional.empty();
        }

        // Either this is an empty declaration, or there may be an assignment?
        Optional<? extends Statement> assignement;
        if (findAssignment(defensiveCopy)) {
            assignement = Statement.build(defensiveCopy);
        } else {
            assignement = Optional.empty();
        }

        // Variable declaration is valid, let's commit the changes to the input reference
        inputRef.set(defensiveCopy.get());
        return Optional.of(new VariableDeclaration(qualifiers, className, varName.get().getVariableName(), assignement));
    }

    private static boolean findAssignment(AtomicReference<String> input) {
        Matcher nameMatcher = assignmentPattern.matcher(input.get());
        if (!nameMatcher.lookingAt()) {
            return false;
        }
        input.set(input.get().substring(nameMatcher.end()));
        JavaWhitespace.skipWhitespaceAndComments(input);
        return true;
    }

    @Override
    public List<String> show(String prefix) {
        final String qualifierString = Qualifiers.toString(qualifiers);
        String decla = (qualifierString.isEmpty() ? "" : qualifierString + " ") + "var[" + name + "]OF{" + type + "}";
        List<String> result = new ArrayList<>();
        if (initialAssignement.isPresent()) {
            List<String> assignmentLines = initialAssignement.get().show("");
            String alignment = new String(new char[decla.length()]).replace("\0", " ");
            for (int i = 0; i < assignmentLines.size(); i++) {
                if (i == 0) {
                    result.add(prefix + decla + "=" + assignmentLines.get(i)); // First line has LHS
                } else {
                    result.add(prefix + alignment + " " + assignmentLines.get(i));   // other lines omit LHS
                }
            }
        } else {
            result.add(prefix + decla); // all-in-one line
        }
        return result;
    }

    @Override
    public <T> T acceptExpressionVisitor(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
