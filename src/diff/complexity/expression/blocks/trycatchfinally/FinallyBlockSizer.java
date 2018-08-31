package diff.complexity.expression.blocks.trycatchfinally;

import diff.complexity.SyntaxSizer;
import diff.complexity.expression.ExpressionSizer;
import lexeme.java.tree.expression.blocks.trycatchfinally.FinallyBlock;

public final class FinallyBlockSizer extends SyntaxSizer<FinallyBlock> {
    public static final FinallyBlockSizer FINALLY_BLOCK_SIZER = new FinallyBlockSizer();

    @Override
    public int size(FinallyBlock obj) {
        return ExpressionSizer.EXPRESSION_SIZER.size(obj.getFinallyExpressions());
    }
}
