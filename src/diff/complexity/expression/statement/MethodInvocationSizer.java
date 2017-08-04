package diff.complexity.expression.statement;

import diff.complexity.SyntaxSizer;
import parser.syntaxtree.expression.statement.MethodInvocation;

public final class MethodInvocationSizer extends SyntaxSizer<MethodInvocation> {
    public static final MethodInvocationSizer METHOD_INVOCATION_SIZER = new MethodInvocationSizer();

    @Override
    public int size(MethodInvocation methodInvocation) {
        int methodName = 1;
        int methodParamsComplexity = StatementSizer.STATEMENT_SIZER.size(methodInvocation.getArguments().getParameters());
        return 1 + methodName + methodParamsComplexity;
    }
}
