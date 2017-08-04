package diff.complexity.expression.blocks.trycatchfinally;

import diff.complexity.SyntaxSizer;
import diff.complexity.expression.ExpressionSizer;
import diff.complexity.expression.VariableDeclarationSizer;
import parser.syntaxtree.expression.blocks.trycatchfinally.TryBlock;

public final class TryBlockSizer extends SyntaxSizer<TryBlock> {
    public static final TryBlockSizer TRY_BLOCK_SIZER = new TryBlockSizer();

    @Override
    public int size(TryBlock obj) {
        int resourcesSize = VariableDeclarationSizer.VARIABLE_DECLARATION_SIZER.size(obj.getTryWithResources());
        int bodySize = ExpressionSizer.EXPRESSION_SIZER.size(obj.getTryExpressions());
        return resourcesSize + bodySize;
    }
}
