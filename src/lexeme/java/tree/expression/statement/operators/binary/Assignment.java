package lexeme.java.tree.expression.statement.operators.binary;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.statement.Statement;
import lombok.Getter;

/**
 * Represents the assignment operator.
 */
@Getter
public class Assignment extends BinaryOperator {

    private static final Pattern assignmentPattern = Pattern.compile("=");

    /**
     * Assignment
     * @param leftHandSide assignee
     * @param rightHandSide value to assign
     */
    public Assignment(Statement leftHandSide, Statement rightHandSide) {
        super(leftHandSide, rightHandSide);
    }

    /**
     * Attempts to build a {@link Assignment}
     * @param lhs the left hand side
     * @param input the remaining text (is modified if the operator is built)
     * @return optionally, the operator
     */
    public static Optional<Assignment> build(Statement lhs, AtomicReference<String> input) {
        if (!lhs.isAssignable()) {
            return Optional.empty();
        }

        AtomicReference<String> defensiveCopy = new AtomicReference<String>(input.get());
        if (!find(defensiveCopy)) {
            return Optional.empty();
        }

        Optional<? extends Statement> rhs = Statement.build(defensiveCopy);
        if (!rhs.isPresent()) {
            return Optional.empty();
        }

        input.set(defensiveCopy.get());
        return Optional.of(new Assignment(lhs, rhs.get()));
    }

    static boolean find(AtomicReference<String> input) {
        Matcher nameMatcher = assignmentPattern.matcher(input.get());
        if (!nameMatcher.lookingAt()) {
            return false;
        }
        input.set(input.get().substring(nameMatcher.end()));
        JavaWhitespace.skipWhitespaceAndComments(input);
        return true;
    }

    @Override
    public String getOperatorSymbol() {
        return "=";
    }

    @Override
    public <T> T accept(BinaryOperatorVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
