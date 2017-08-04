package parser.syntaxtree.expression.statement.operators.unary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.expression.statement.Statement;
import parser.syntaxtree.expression.statement.operators.Operator;
import parser.syntaxtree.expression.statement.operators.OperatorVisitor;

/**
 * Unary Operator like <code>!</code>.
 */
@Getter
@AllArgsConstructor
public abstract class UnaryOperator extends Operator {

    protected final Statement targetedStatement;

    @Override
    public <T> T acceptOperatorVisitor(OperatorVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Accept a {@link UnaryOperatorVisitor}.
     * @param <T> the outcome type of the visit
     * @param visitor the visitor
     * @return the outcome of the visit
     */
    public abstract <T> T acceptUnaryOperatorVisitor(UnaryOperatorVisitor<T> visitor);

}
