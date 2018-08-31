package diff.complexity.expression.blocks.trycatchfinally;

import diff.complexity.ClassNameSizer;
import diff.complexity.SyntaxSizer;
import diff.complexity.expression.ExpressionSizer;
import diff.complexity.expression.statement.VariableReferenceSizer;
import lexeme.java.tree.expression.blocks.trycatchfinally.CatchBlock;

public final class CatchBlockSizer extends SyntaxSizer<CatchBlock> {
    public static final CatchBlockSizer CATCH_BLOCK_SIZER = new CatchBlockSizer();

    @Override
    public int size(CatchBlock obj) {
        int exceptionTypeSize = ClassNameSizer.CLASS_NAME_SIZER.size(obj.getExceptionTypes());
        int exceptionNameSize = VariableReferenceSizer.VARIABLE_REFERENCE_SIZER.size(obj.getExceptionReference());
        int bodySize = ExpressionSizer.EXPRESSION_SIZER.size(obj.getCatchExpressions());
        return exceptionTypeSize + exceptionNameSize + bodySize;
    }
}
