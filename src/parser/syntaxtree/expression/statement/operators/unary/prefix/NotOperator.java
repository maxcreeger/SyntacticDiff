package parser.syntaxtree.expression.statement.operators.unary.prefix;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import parser.syntaxtree.Whitespace;
import parser.syntaxtree.expression.statement.Statement;
import parser.syntaxtree.expression.statement.operators.unary.UnaryOperatorVisitor;

/**
 * The "not" (<code>!</code>) operator.
 */
@Getter
public class NotOperator extends PrefixUnaryOperator {

    private static final String OPERATOR_SYMBOL = "!";

    NotOperator(Statement rightHandSide) {
        super(rightHandSide);
    }

    private static final Pattern operatorPattern = Pattern.compile(OPERATOR_SYMBOL);

    public static Optional<NotOperator> build(AtomicReference<String> input) {
        if (!find(input)) {
            return Optional.empty();
        }

        Optional<? extends Statement> rhs = Statement.build(input);
        if (!rhs.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(new NotOperator(rhs.get()));

    }

    static boolean find(AtomicReference<String> input) {
        Matcher nameMatcher = operatorPattern.matcher(input.get());
        if (!nameMatcher.lookingAt()) {
            return false;
        }
        input.set(input.get().substring(nameMatcher.end()));
        Whitespace.skipWhitespaceAndComments(input);
        return true;
    }

    @Override
    public <T> T acceptUnaryOperatorVisitor(UnaryOperatorVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getOperatorSymbol() {
        return OPERATOR_SYMBOL;
    }

}

