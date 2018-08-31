package lexeme.java.tree.expression.statement.operators.binary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.operators.Operator;
import lexeme.java.tree.expression.statement.operators.OperatorVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a binary operator like <code>+</code>.
 */
@Getter
@AllArgsConstructor
public abstract class BinaryOperator extends Operator {

    protected final Statement leftHandSide;
    protected final Statement rightHandSide;

    /**
     * Attempts to build a {@link BinaryOperator} on from an input text, on a known left hand side {@link Statement}.
     * @param leftHandSide the left hand side statement
     * @param input the mutable input text (is modified if the object is built)
     * @return optionally, a {@link BinaryOperator}
     */
    public static Optional<? extends BinaryOperator> build(Statement leftHandSide, AtomicReference<String> input) {

        Optional<? extends BinaryOperator> opt = Addition.build(leftHandSide, input);
        if (opt.isPresent()) {
            return opt;
        }

        opt = BooleanAnd.build(leftHandSide, input);
        if (opt.isPresent()) {
            return opt;
        }

        opt = Assignment.build(leftHandSide, input);
        if (opt.isPresent()) {
            return opt;
        }

        opt = Division.build(leftHandSide, input);
        if (opt.isPresent()) {
            return opt;
        }

        opt = Equals.build(leftHandSide, input);
        if (opt.isPresent()) {
            return opt;
        }

        opt = Different.build(leftHandSide, input);
        if (opt.isPresent()) {
            return opt;
        }

        opt = Multiply.build(leftHandSide, input);
        if (opt.isPresent()) {
            return opt;
        }

        opt = BooleanOr.build(leftHandSide, input);
        if (opt.isPresent()) {
            return opt;
        }

        opt = Subtraction.build(leftHandSide, input);
        if (opt.isPresent()) {
            return opt;
        }

        return Optional.empty();
    }

    @Override
    public <T> T acceptOperatorVisitor(OperatorVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Accepts a {@link BinaryOperatorVisitor}.
     * @param <T> the output type
     * @param visitor the visitor
     * @return the outcome
     */
    public abstract <T> T accept(BinaryOperatorVisitor<T> visitor);

    @Override
    public List<String> show(String prefix) {
        List<String> leftStr = leftHandSide.show("");
        List<String> rightStr = rightHandSide.show("");
        final String operatorSymbol = getOperatorSymbol();
        if (leftStr.size() == 1 && rightStr.size() == 1) {
            return Arrays.asList(prefix + leftStr.get(0) + " " + operatorSymbol + " " + rightStr.get(0));
        } else {
            List<String> result = new ArrayList<>();
            for (String line : leftStr) {
                result.add(prefix + "   " + line);
            }
            result.add(prefix + operatorSymbol);
            for (String line : rightStr) {
                result.add(prefix + "   " + line);
            }
            return result;
        }
    }

}
