package lexeme.java.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import diff.complexity.Showable;
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
 * A Complete method declaration, with qualifiers, return type, name, parameter types, and body.
 */
@AllArgsConstructor
@Getter
public class MethodDeclaration implements Showable, Structure<JavaGrammar> {

    private final List<Qualifiers> qualifiers;
    private final ClassName returnType;
    private final String name;
    private final List<ParameterTypeDeclaration> parameters;
    private final List<Expression> expressions;
    private final CodeLocation location;

    /**
     * Attempts to build a {@link MethodDeclaration}.
     * @param inputRef the input text (is mutated if a method declaration is built)
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
        return Optional.of(new MethodDeclaration(qualifiers, returnType.get(), methodName.get().getVariableName(), parameters.get(), expressions,
                defensiveCopy.commit()));
    }

    public List<JavaQualifier> getQualifierEnums() {
        return qualifiers.stream().map(quali -> quali.qualifier).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.join("\n", show(""));
    }

    @Override
    public List<String> show(String prefix) {
        List<String> result = new ArrayList<>();
        // Qualifiers
        String qualif = Qualifiers.toString(qualifiers);
        // Method parameters
        if (parameters.isEmpty()) {
            result.add(prefix + qualif + " " + returnType.toString() + " " + name + "( ) {"); // No method parameters
        } else {
            result.add(prefix + qualif + " " + returnType.toString() + " " + name + "(");
            for (ParameterTypeDeclaration param : parameters) {
                result.addAll(param.show(prefix + "m  p  ")); // list method parameters
            }
            result.add(prefix + "m  ) {");
        }
        // Method body
        String bodyPrefix = prefix + "m  b  ";
        for (Expression expr : expressions) {
            result.addAll(expr.show(bodyPrefix));
        }
        result.add(prefix + "}");
        return result;
    }
}
