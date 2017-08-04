package diff.similarity.evaluator.expression.blocks;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.expression.ExpressionSizer;
import diff.complexity.expression.blocks.trycatchfinally.CatchBlockSizer;
import diff.complexity.expression.blocks.trycatchfinally.FinallyBlockSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.NoSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import diff.similarity.evaluator.expression.blocks.trycatchfinally.CatchBlockSimilarityEvaluator;
import diff.similarity.evaluator.expression.blocks.trycatchfinally.FinallyBlockSimilarityEvaluator;
import diff.similarity.evaluator.expression.blocks.trycatchfinally.TryBlockSimilarityEvaluator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.VariableDeclaration;
import parser.syntaxtree.expression.blocks.AbstractBlock;
import parser.syntaxtree.expression.blocks.BlockVisitor;
import parser.syntaxtree.expression.blocks.DoWhileBlock;
import parser.syntaxtree.expression.blocks.ForBlock;
import parser.syntaxtree.expression.blocks.IfBlock;
import parser.syntaxtree.expression.blocks.WhileBlock;
import parser.syntaxtree.expression.blocks.trycatchfinally.FinallyBlock;
import parser.syntaxtree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;

/** Compares a {@link TryCatchFinallyBlock} to any other {@link AbstractBlock} to produce a {@link Similarity}. */
@AllArgsConstructor
@Getter
public class TryCatchFinallyBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

    TryCatchFinallyBlock rightTryCatchFinallyBlock;

    @Override
    public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
        Similarity trySim =
                TryBlockSimilarityEvaluator.INSTANCE.eval(leftTryCatchFinallyBlock.getTryBlock(), rightTryCatchFinallyBlock.getTryBlock());
        Similarity catchSim = CatchBlockSimilarityEvaluator.INSTANCE.maximumMatch(leftTryCatchFinallyBlock.getCatchBlocks(),
                rightTryCatchFinallyBlock.getCatchBlocks());
        Similarity finallySim = FinallyBlockSimilarityEvaluator.INSTANCE.eval(leftTryCatchFinallyBlock.getFinallyBlock(),
                rightTryCatchFinallyBlock.getFinallyBlock());
        return Similarity.add("TryCatchFinally", trySim, catchSim, finallySim);
    }

    @Override
    public Similarity visit(WhileBlock leftWhileBlock) {
        Similarity wrongBlockSim = new SimpleSimilarity(0, 1, "while", "0", "trycatchfinally");
        return encloseLeft(leftWhileBlock, wrongBlockSim);
    }

    @Override
    public Similarity visit(IfBlock leftIfBlock) {
        Similarity wrongBlockSim = new SimpleSimilarity(0, 1, "if", "0", "trycatchfinally");
        return encloseLeft(leftIfBlock, wrongBlockSim);
    }

    @Override
    public Similarity visit(ForBlock leftForBlock) {
        Similarity wrongBlockSim = new SimpleSimilarity(0, 1, "for", "0", "trycatchfinally");
        return encloseLeft(leftForBlock, wrongBlockSim);
    }

    @Override
    public Similarity visit(DoWhileBlock leftDoWhileBlock) {
        Similarity wrongBlockSim = new SimpleSimilarity(0, 1, "do", "0", "trycatchfinally");
        return encloseLeft(leftDoWhileBlock, wrongBlockSim);
    }

    /**
     * Enclose a left {@link Expression} in this {@link TryCatchFinallyBlock} on the right.
     * @param leftExpression an {@link Expression}
     * @param wrongBlockSim header {@link Similarity} to tell there is a difference (the enclosing)
     * @return a {@link Similarity}
     */
    public Similarity encloseLeft(Expression leftExpression, Similarity wrongBlockSim) {
        // Right leaf: resources
        List<VariableDeclaration> tryWithResources = rightTryCatchFinallyBlock.getTryBlock().getTryWithResources();
        List<Expression> tryWithResourcesExpressions = new ArrayList<Expression>(tryWithResources);
        Similarity resourceSim =
                RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(tryWithResourcesExpressions), tryWithResourcesExpressions);
        // Body comparison
        List<Expression> leftWhileInList = new ArrayList<>();
        leftWhileInList.add(leftExpression);
        Similarity bodySim =
                ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftWhileInList, rightTryCatchFinallyBlock.getTryBlock().getTryExpressions());
        // Right leaf: catch & finally
        Similarity catchSim = RightLeafSimilarity.build(CatchBlockSizer.CATCH_BLOCK_SIZER.size(rightTryCatchFinallyBlock.getCatchBlocks()),
                rightTryCatchFinallyBlock.getCatchBlocks());
        Similarity finallySim = rightTryCatchFinallyBlock.getFinallyBlock().isPresent()
                ? new RightLeafSimilarity<FinallyBlock>(FinallyBlockSizer.FINALLY_BLOCK_SIZER.size(rightTryCatchFinallyBlock.getFinallyBlock().get()),
                        rightTryCatchFinallyBlock.getFinallyBlock().get())
                : new NoSimilarity();
        return Similarity.add("trans-block", wrongBlockSim, resourceSim, bodySim, catchSim, finallySim);
    }



    /**
     * Enclose a right {@link Expression} in a {@link TryCatchFinallyBlock} on the left.
     * @param leftTryCatchFinallyBlock the left {@link TryCatchFinallyBlock}
     * @param rightExpression an {@link Expression}
     * @param wrongBlockSim header {@link Similarity} to tell there is a difference (the enclosing)
     * @return a {@link Similarity}
     */
    public static Similarity encloseRight(TryCatchFinallyBlock leftTryCatchFinallyBlock, Expression rightExpression, Similarity wrongBlockSim) {
        // Right leaf: resources
        List<VariableDeclaration> tryWithResources = leftTryCatchFinallyBlock.getTryBlock().getTryWithResources();
        List<Expression> tryWithResourcesExpressions = new ArrayList<Expression>(tryWithResources);
        Similarity resourceSim =
                LeftLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(tryWithResourcesExpressions), tryWithResourcesExpressions);
        // Body comparison
        List<Expression> rightExpressionInList = new ArrayList<>();
        rightExpressionInList.add(rightExpression);
        Similarity bodySim =
                ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftTryCatchFinallyBlock.getTryBlock().getTryExpressions(), rightExpressionInList);
        // Right leaf: catch & finally
        Similarity catchSim = LeftLeafSimilarity.build(CatchBlockSizer.CATCH_BLOCK_SIZER.size(leftTryCatchFinallyBlock.getCatchBlocks()),
                leftTryCatchFinallyBlock.getCatchBlocks());
        Similarity finallySim = leftTryCatchFinallyBlock.getFinallyBlock().isPresent()
                ? new LeftLeafSimilarity<FinallyBlock>(FinallyBlockSizer.FINALLY_BLOCK_SIZER.size(leftTryCatchFinallyBlock.getFinallyBlock().get()),
                        leftTryCatchFinallyBlock.getFinallyBlock().get())
                : new NoSimilarity();
        return Similarity.add("trans-block", wrongBlockSim, resourceSim, bodySim, catchSim, finallySim);
    }

}
