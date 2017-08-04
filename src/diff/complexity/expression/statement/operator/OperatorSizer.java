package diff.complexity.expression.statement.operator;

import diff.complexity.SyntaxSizer;
import parser.syntaxtree.expression.statement.operators.Operator;
import parser.syntaxtree.expression.statement.operators.OperatorVisitor;
import parser.syntaxtree.expression.statement.operators.binary.BinaryOperator;
import parser.syntaxtree.expression.statement.operators.unary.UnaryOperator;

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
