package diff.similarity.evaluator.expression;

import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.blocks.AbstractBlockSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.StatementSimilarityEvaluator;
import lombok.AllArgsConstructor;
import parser.syntaxtree.expression.EmptyExpression;
import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.ExpressionVisitor;
import parser.syntaxtree.expression.VariableDeclaration;
import parser.syntaxtree.expression.blocks.AbstractBlock;
import parser.syntaxtree.expression.statement.Statement;

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
