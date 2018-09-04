package lexeme.java.tree.expression.statement.operators.unary.prefix;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.operators.unary.UnaryOperatorVisitor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;

/**
 * The "not" (<code>!</code>) operator.
 */
@Getter
public class NotOperator extends PrefixUnaryOperator {

    private static final String OPERATOR_SYMBOL = "!";

    /**
     * Construct a {@link NotOperator}
     * @param rightHandSide rhs
     */
    public NotOperator(Statement rightHandSide) {
        super(rightHandSide);
    }

    private static final Pattern operatorPattern = Pattern.compile(OPERATOR_SYMBOL);

    public static Optional<NotOperator> build(CodeBranch input) {
        if (!find(input)) {
            return Optional.empty();
        }

        Optional<? extends Statement> rhs = Statement.build(input);
        if (!rhs.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(new NotOperator(rhs.get()));

    }

    static boolean find(CodeBranch input) {
        Matcher nameMatcher = operatorPattern.matcher(input.getRest());
        if (!nameMatcher.lookingAt()) {
            return false;
        }
        input.advance(nameMatcher.end());
        JavaWhitespace.skipWhitespaceAndComments(input);
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

