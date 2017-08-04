package parser.syntaxtree.expression.statement.operators.unary.prefix;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Getter;
import parser.syntaxtree.expression.statement.Statement;
import parser.syntaxtree.expression.statement.operators.unary.UnaryOperator;

/**
 * Represents a prefix unary operator like <code>++a</code> or <code>!isEmpty()</code>.
 */
@Getter
public abstract class PrefixUnaryOperator extends UnaryOperator {

    PrefixUnaryOperator(Statement targetedStatement) {
        super(targetedStatement);
    }

    /**
     * Attempts to build a {@link PrefixUnaryOperator} from an input text.
     * @param input the mutable input text (is modified if the object is built)
     * @return optionally, the operator
     */
    public static Optional<? extends PrefixUnaryOperator> buildUnaryPrefix(AtomicReference<String> input) {
        Optional<? extends PrefixUnaryOperator> opt = NotOperator.build(input);
        if (opt.isPresent()) {
            return opt;
        }

        opt = PreIncrementOperator.build(input);
        if (opt.isPresent()) {
            return opt;
        }

        return Optional.empty();
    }

    @Override
    public List<String> show(String prefix) {
        final List<String> result = targetedStatement.show("");
        final String operatorSymbol = getOperatorSymbol();
        for (int i = 0; i < result.size(); i++) {
            if (i == 0) {
                result.set(i, prefix + operatorSymbol + result.get(i));
            } else {
                result.set(i, prefix + operatorSymbol.length() + result.get(i));
            }
        }
        return result;
    }
}
