package diff.complexity.expression.statement.operator;

import diff.complexity.SyntaxSizer;
import diff.complexity.expression.statement.StatementSizer;
import lexeme.java.tree.expression.statement.operators.binary.BinaryOperator;

public final class BinaryOperatorSizer extends SyntaxSizer<BinaryOperator> {
    public static final BinaryOperatorSizer BINARY_OPERATOR_SIZER = new BinaryOperatorSizer();

    @Override
    public int size(BinaryOperator obj) {
        return StatementSizer.STATEMENT_SIZER.size(obj.getLeftHandSide(), obj.getRightHandSide()) + 1;
    }
}
