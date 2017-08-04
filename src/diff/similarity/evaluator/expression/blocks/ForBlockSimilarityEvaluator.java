package diff.similarity.evaluator.expression.blocks;

import diff.complexity.expression.ExpressionSizer;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.StatementSimilarityEvaluator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.expression.blocks.AbstractBlock;
import parser.syntaxtree.expression.blocks.BlockVisitor;
import parser.syntaxtree.expression.blocks.DoWhileBlock;
import parser.syntaxtree.expression.blocks.ForBlock;
import parser.syntaxtree.expression.blocks.IfBlock;
import parser.syntaxtree.expression.blocks.WhileBlock;
import parser.syntaxtree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;

/** Compares a {@link ForBlock} to any other {@link AbstractBlock} to produce a {@link Similarity}. */
@AllArgsConstructor
@Getter
public class ForBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

    ForBlock rightForBlock;

    @Override
    public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Similarity visit(WhileBlock leftWhileBlock) {
        Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "while", "0", "for");
        Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftWhileBlock.getEvaluation(), rightForBlock.getEvaluation());
        Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftWhileBlock.getBody(), rightForBlock.getBody());
        return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
    }

    @Override
    public Similarity visit(IfBlock leftIfBlock) {
        Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "if", "0", "while");
        Similarity simCondition = StatementSimilarityEvaluator.INSTANCE.eval(leftIfBlock.getCondition(), rightForBlock.getEvaluation());
        Similarity simForThen = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getThenExpressions(), rightForBlock.getBody());
        Similarity simForElse = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getElseExpressions(), rightForBlock.getBody());
        Similarity thenSim;
        Similarity elseSim;
        if (simForThen.similarity() < simForElse.similarity()) {
            thenSim = LeftLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(leftIfBlock.getThenExpressions()),
                    leftIfBlock.getThenExpressions());
            elseSim = simForElse;
        } else {
            thenSim = simForThen;
            elseSim = LeftLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(leftIfBlock.getElseExpressions()),
                    leftIfBlock.getElseExpressions());
        }
        return Similarity.add("trans-block", wrongBlockSimilarity, simCondition, thenSim, elseSim);
    }

    @Override
    public Similarity visit(ForBlock leftForBlock) {
        Similarity initDiff = ExpressionSimilarityEvaluator.INSTANCE.eval(leftForBlock.getInitialisation(), rightForBlock.getInitialisation());
        Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftForBlock.getEvaluation(), rightForBlock.getEvaluation());
        Similarity iterDiff = ExpressionSimilarityEvaluator.INSTANCE.eval(leftForBlock.getIteration(), rightForBlock.getIteration());
        Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftForBlock.getBody(), rightForBlock.getBody());
        return Similarity.add("for", initDiff, evalDiff, iterDiff, bodyDiff);
    }

    @Override
    public Similarity visit(DoWhileBlock leftDoWhileBlock) {
        Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "do", "0", "for");
        Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftDoWhileBlock.getEvaluation(), rightForBlock.getEvaluation());
        Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftDoWhileBlock.getBody(), rightForBlock.getBody());
        return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
    }

}
