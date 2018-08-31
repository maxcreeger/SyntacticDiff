package lexeme.java.tree.expression.statement.operators.binary;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.statement.Statement;
import lombok.Getter;

/**
 * Represents the addition operator.
 */
@Getter
public class Addition extends BinaryOperator {

    /**
     * Construct an addition
     * @param leftHandSide lhs
     * @param rightHandSide rhs
     */
    public Addition(Statement leftHandSide, Statement rightHandSide) {
        super(leftHandSide, rightHandSide);
    }

    private static final Pattern operatorPattern = Pattern.compile("\\+");

    /**
     * Attempts to build a {@link Addition}
     * @param lhs the left hand side
     * @param input the remaining text (is modified if the operator is built)
     * @return optionally, the operator
     */
    public static Optional<Addition> build(Statement lhs, AtomicReference<String> input) {
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(input.get());
        if (!find(defensiveCopy)) {
            return Optional.empty();
        }

        Optional<? extends Statement> rhs = Statement.build(defensiveCopy);
        if (!rhs.isPresent()) {
            return Optional.empty();
        }

        input.set(defensiveCopy.get());
        return Optional.of(new Addition(lhs, rhs.get()));
    }

    private static boolean find(AtomicReference<String> input) {
        Matcher nameMatcher = operatorPattern.matcher(input.get());
        if (!nameMatcher.lookingAt()) {
            return false;
        }
        input.set(input.get().substring(nameMatcher.end()));
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
