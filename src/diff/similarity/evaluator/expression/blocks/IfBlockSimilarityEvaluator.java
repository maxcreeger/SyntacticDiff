package diff.similarity.evaluator.expression.blocks;

import diff.complexity.expression.ExpressionSizer;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.SimpleSimilarity;
import diff.similarity.evaluator.expression.ExpressionSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.StatementSimilarityEvaluator;
import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.blocks.BlockVisitor;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Compares a {@link IfBlock} to any other {@link AbstractBlock} to produce a {@link Similarity}. */
@AllArgsConstructor
@Getter
public class IfBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

    IfBlock rightIfBlock;

    @Override
    public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Similarity visit(WhileBlock leftWhileBlock) {
        Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "while", "0", "if");
        Similarity simCondition = StatementSimilarityEvaluator.INSTANCE.eval(leftWhileBlock.getEvaluation(), rightIfBlock.getCondition());
        Similarity simForThen = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftWhileBlock.getBody(), rightIfBlock.getThenExpressions());
        Similarity simForElse = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftWhileBlock.getBody(), rightIfBlock.getElseExpressions());
        Similarity thenSim;
        Similarity elseSim;
        if (simForThen.similarity() < simForElse.similarity()) {
            thenSim = RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(rightIfBlock.getThenExpressions()),
                    rightIfBlock.getThenExpressions());
            elseSim = simForElse;
        } else {
            thenSim = simForThen;
            elseSim = RightLeafSimilarity.build(ExpressionSizer.EXPRESSION_SIZER.size(rightIfBlock.getElseExpressions()),
                    rightIfBlock.getElseExpressions());
        }
        return Similarity.add("trans-block", wrongBlockSimilarity, simCondition, thenSim, elseSim);
    }

    @Override
    public Similarity visit(IfBlock leftIfBlock) {
        Similarity condDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftIfBlock.getCondition(), rightIfBlock.getCondition());
        Similarity thenDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getThenExpressions(), rightIfBlock.getThenExpressions());
        Similarity elseDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftIfBlock.getElseExpressions(), rightIfBlock.getElseExpressions());
        return Similarity.add("if", condDiff, thenDiff, elseDiff);
    }

    @Override
    public Similarity visit(ForBlock leftForBlock) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Similarity visit(DoWhileBlock leftDoWhileBlock) {
        // TODO Auto-generated method stub
        return null;
    }
}
