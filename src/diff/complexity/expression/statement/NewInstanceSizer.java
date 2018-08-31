package diff.complexity.expression.statement;

import diff.complexity.ClassNameSizer;
import diff.complexity.SyntaxSizer;
import lexeme.java.tree.expression.statement.NewInstance;

public final class NewInstanceSizer extends SyntaxSizer<NewInstance> {
    public static final NewInstanceSizer NEW_INSTANCE_SIZER = new NewInstanceSizer();

    @Override
    public int size(NewInstance obj) {
        int classNameSize = ClassNameSizer.CLASS_NAME_SIZER.size(obj.getClassName());
        int paramsSize = StatementSizer.STATEMENT_SIZER.size(obj.getConstructorArguments().getParameters());
        return classNameSize + paramsSize + 1;
    }
}
