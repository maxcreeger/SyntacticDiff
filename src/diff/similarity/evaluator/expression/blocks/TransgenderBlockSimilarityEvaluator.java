package diff.similarity.evaluator.expression.blocks;

import diff.similarity.Similarity;
import lombok.AllArgsConstructor;
import parser.syntaxtree.expression.blocks.AbstractBlock;
import parser.syntaxtree.expression.blocks.BlockVisitor;
import parser.syntaxtree.expression.blocks.DoWhileBlock;
import parser.syntaxtree.expression.blocks.ForBlock;
import parser.syntaxtree.expression.blocks.IfBlock;
import parser.syntaxtree.expression.blocks.WhileBlock;
import parser.syntaxtree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;

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
