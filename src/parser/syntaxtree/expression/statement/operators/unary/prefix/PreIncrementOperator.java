package parser.syntaxtree.expression.statement.operators.unary.prefix;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import parser.syntaxtree.Whitespace;
import parser.syntaxtree.expression.statement.Statement;
import parser.syntaxtree.expression.statement.operators.OperatorVisitor;
import parser.syntaxtree.expression.statement.operators.unary.UnaryOperatorVisitor;

/**
 * Pre-increment operator like <code>++a</code>. Increments, then reads the value
 */
@Getter
public class PreIncrementOperator extends PrefixUnaryOperator {

    /**
     * builds an {@link PreIncrementOperator} on a {@link Statement}.
     * @param rightHandSide the statement
     */
    public PreIncrementOperator(Statement rightHandSide) {
        super(rightHandSide);
    }

    private static final Pattern operatorPattern = Pattern.compile("\\+\\+");

    public static Optional<PreIncrementOperator> build(AtomicReference<String> input) {
        if (!find(input)) {
            return Optional.empty();
        }

        Optional<? extends Statement> rhs = Statement.build(input);
        if (!rhs.isPresent() || !rhs.get().isAssignable()) {
            return Optional.empty();
        }
        return Optional.of(new PreIncrementOperator(rhs.get()));

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
    public <T> T acceptOperatorVisitor(OperatorVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T acceptUnaryOperatorVisitor(UnaryOperatorVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getOperatorSymbol() {
        return "++";
    }

}
