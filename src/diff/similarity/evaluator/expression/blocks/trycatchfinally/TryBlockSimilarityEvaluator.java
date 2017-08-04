package diff.similarity.evaluator.expression.blocks.trycatchfinally;

import diff.complexity.expression.blocks.trycatchfinally.TryBlockSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import diff.similarity.evaluator.expression.VariableDeclarationSimilarityEvaluator;
import parser.syntaxtree.expression.blocks.trycatchfinally.TryBlock;

/**
 * Compares two {@link TryBlock}s.
 */
public class TryBlockSimilarityEvaluator extends SimilarityEvaluator<TryBlock> {

    /** Instance */
    public static final TryBlockSimilarityEvaluator INSTANCE = new TryBlockSimilarityEvaluator();

    private TryBlockSimilarityEvaluator() {
        super(TryBlockSizer.TRY_BLOCK_SIZER, "try");
    }

    @Override
    public Similarity eval(TryBlock obj1, TryBlock obj2) {
        Similarity resourcesSim = VariableDeclarationSimilarityEvaluator.INSTANCE
                .orderedEval(obj1.getTryWithResources(), obj2.getTryWithResources());
        Similarity bodySim = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(obj1.getTryExpressions(), obj2.getTryExpressions());
        return Similarity.add("try", resourcesSim, bodySim);
    }

}