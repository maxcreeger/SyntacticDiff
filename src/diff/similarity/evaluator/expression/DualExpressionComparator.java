package diff.similarity.evaluator.expression;

import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.blocks.AbstractBlockSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.StatementSimilarityEvaluator;
import lexeme.java.tree.expression.EmptyExpression;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.ExpressionVisitor;
import lexeme.java.tree.expression.VariableDeclaration;
import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.statement.Statement;
import lombok.AllArgsConstructor;

/**
 * Compares to types of {@link Expression}s together. They must be of the same sub-type
 *
 * @param <T> the common sub-type
 */
@AllArgsConstructor
public class DualExpressionComparator<T extends Expression> implements ExpressionVisitor<Similarity> {

    private final T expr2;

    @Override
    public Similarity visit(EmptyExpression emptyExpression) {
        return EmptyExpressionSimilarityEvaluator.INSTANCE.eval(emptyExpression, (EmptyExpression) expr2);
    }

    @Override
    public Similarity visit(AbstractBlock abstractBlock) {
        return AbstractBlockSimilarityEvaluator.INSTANCE.eval(abstractBlock, (AbstractBlock) expr2);
    }

    @Override
    public Similarity visit(VariableDeclaration varDec) {
        return VariableDeclarationSimilarityEvaluator.INSTANCE.eval(varDec, (VariableDeclaration) expr2);
    }

    @Override
    public Similarity visit(Statement statement) {
        return StatementSimilarityEvaluator.INSTANCE.eval(statement, (Statement) expr2);
    }

}
