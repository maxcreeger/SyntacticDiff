package diff.complexity.expression.statement.operator;

import diff.complexity.SyntaxSizer;
import lexeme.java.tree.expression.statement.operators.Operator;
import lexeme.java.tree.expression.statement.operators.OperatorVisitor;
import lexeme.java.tree.expression.statement.operators.binary.BinaryOperator;
import lexeme.java.tree.expression.statement.operators.unary.UnaryOperator;

public final class OperatorSizer extends SyntaxSizer<Operator> implements OperatorVisitor<Integer> {
    public static final OperatorSizer OPERATOR_SIZER = new OperatorSizer();

    @Override
    public int size(Operator obj) {
        return obj.acceptOperatorVisitor(this);
    }

    @Override
    public Integer visit(UnaryOperator unaryOperator) {
        return UnaryOperatorSizer.UNARY_OPERATOR_SIZER.size(unaryOperator);
    }

    @Override
    public Integer visit(BinaryOperator binaryOperator) {
        return BinaryOperatorSizer.BINARY_OPERATOR_SIZER.size(binaryOperator);
    }

}
