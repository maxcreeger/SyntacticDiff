package diff.similarity.evaluator.expression.blocks;

import diff.similarity.Similarity;
import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.blocks.BlockVisitor;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import lombok.AllArgsConstructor;

/** Compares a {@link TryCatchFinallyBlock} to any other {@link AbstractBlock} to produce a {@link Similarity}. */
@AllArgsConstructor
public class TransgenderBlockSimilarityEvaluator implements BlockVisitor<Similarity> {

    AbstractBlock leftBlock;

    @Override
    public Similarity visit(TryCatchFinallyBlock rightTryCatchFinallyBlock) {
        return leftBlock.acceptBlockVisitor(new TryCatchFinallyBlockSimilarityEvaluator(rightTryCatchFinallyBlock));
    }

    @Override
    public Similarity visit(WhileBlock rightWhileBlock) {
        return leftBlock.acceptBlockVisitor(new WhileBlockSimilarityEvaluator(rightWhileBlock));
    }

    @Override
    public Similarity visit(IfBlock rightIfBlock) {
        return leftBlock.acceptBlockVisitor(new IfBlockSimilarityEvaluator(rightIfBlock));
    }

    @Override
    public Similarity visit(ForBlock rightForBlock) {
        return leftBlock.acceptBlockVisitor(new ForBlockSimilarityEvaluator(rightForBlock));
    }

    @Override
    public Similarity visit(DoWhileBlock rightDoWhileBlock) {
        return leftBlock.acceptBlockVisitor(new DoWhileBlockSimilarityEvaluator(rightDoWhileBlock));
    }
}
