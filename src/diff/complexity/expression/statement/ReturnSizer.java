package diff.complexity.expression.statement;

import diff.complexity.SyntaxSizer;
import parser.syntaxtree.expression.statement.Return;

public final class ReturnSizer extends SyntaxSizer<Return> {
    public static final ReturnSizer RETURN_SIZER = new ReturnSizer();

    @Override
    public int size(Return return1) {
        if (return1.getReturnedValue().isPresent()) {
            int returnStatementEvaluationComplexity = StatementSizer.STATEMENT_SIZER.size(return1.getReturnedValue().get());
            return returnStatementEvaluationComplexity + 1;
        } else {
            return 1;
        }
    }
}
