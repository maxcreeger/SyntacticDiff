package diff.complexity.expression.statement.operator;

import diff.complexity.SyntaxSizer;
import diff.complexity.expression.statement.StatementSizer;
import lexeme.java.tree.expression.statement.operators.unary.UnaryOperator;

public final class UnaryOperatorSizer extends SyntaxSizer<UnaryOperator> {
    public static final UnaryOperatorSizer UNARY_OPERATOR_SIZER = new UnaryOperatorSizer();

    @Override
    public int size(UnaryOperator obj) {
		return StatementSizer.STATEMENT_SIZER.size(obj.getTargetedStatement()) + 1;
    }
}
