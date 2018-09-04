package lexeme.java.tree.expression.statement.operators.binary;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.statement.Statement;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents the addition operator.
 */
@Getter
public class Addition extends BinaryOperator {

    private static final Pattern operatorPattern = Pattern.compile("\\+");

    /**
     * Construct an addition
     * @param leftHandSide lhs
     * @param rightHandSide rhs
     */
    public Addition(Statement leftHandSide, Statement rightHandSide, CodeLocation location) {
        super(leftHandSide, rightHandSide, location);
    }

    /**
     * Attempts to build a {@link Addition}
     * @param lhs the left hand side
     * @param input the remaining text (is modified if the operator is built)
     * @return optionally, the operator
     */
    public static Optional<Addition> build(Statement lhs, CodeBranch input) {
        CodeBranch defensiveCopy = input.fork();
        if (!find(defensiveCopy)) {
            return Optional.empty();
        }

        Optional<? extends Statement> rhs = Statement.build(defensiveCopy);
        if (!rhs.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new Addition(lhs, rhs.get(), defensiveCopy.commit()));
    }

    private static boolean find(CodeBranch input) {
        Matcher nameMatcher = operatorPattern.matcher(input.getRest());
        if (!nameMatcher.lookingAt()) {
            return false;
        }
        input.advance(nameMatcher.end());
        JavaWhitespace.skipWhitespaceAndComments(input);
        return true;
    }

    @Override
    public <T> T accept(BinaryOperatorVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getOperatorSymbol() {
        return "+";
    }

}
