package diff.similarity.evaluator.expression.blocks;

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

/** Compares a {@link WhileBlock} to any other {@link AbstractBlock} to produce a {@link Similarity}. */
@AllArgsConstructor
@Getter
public class WhileBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

    WhileBlock rightWhileBlock;

    @Override
    public Similarity visit(TryCatchFinallyBlock leftTryCatchFinallyBlock) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Similarity visit(WhileBlock leftWhileBlock) {
        Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftWhileBlock.getEvaluation(), rightWhileBlock.getEvaluation());
        Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftWhileBlock.getBody(), rightWhileBlock.getBody());
        return Similarity.add("while", evalDiff, bodyDiff);
    }

    @Override
    public Similarity visit(IfBlock leftIfBlock) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Similarity visit(ForBlock leftForBlock) {
        Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "for", "0", "while");
        Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftForBlock.getEvaluation(), rightWhileBlock.getEvaluation());
        Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftForBlock.getBody(), rightWhileBlock.getBody());
        return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
    }

    @Override
    public Similarity visit(DoWhileBlock leftDoWhileBlock) {
        Similarity wrongBlockSimilarity = new SimpleSimilarity(0, 1, "do", "0", "while");
        Similarity evalDiff = StatementSimilarityEvaluator.INSTANCE.eval(leftDoWhileBlock.getEvaluation(), rightWhileBlock.getEvaluation());
        Similarity bodyDiff = ExpressionSimilarityEvaluator.INSTANCE.orderedEval(leftDoWhileBlock.getBody(), rightWhileBlock.getBody());
        return Similarity.add("trans-block", wrongBlockSimilarity, evalDiff, bodyDiff);
    }
}
