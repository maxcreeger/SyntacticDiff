package lexeme.java.tree.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lexeme.java.tree.ClassName;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.Qualifiers;
import lexeme.java.tree.Qualifiers.JavaQualifier;
import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.VariableReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

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
    private final CodeLocation location;

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
        return Optional.of(new VariableDeclaration(qualifiers, className, varName.get().getVariableName(), assignement, fork.commit()));
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
