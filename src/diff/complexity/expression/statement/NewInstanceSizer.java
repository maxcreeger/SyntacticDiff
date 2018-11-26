package diff.complexity.expression.statement;

import diff.complexity.ClassNameSizer;
import diff.complexity.SyntaxSizer;
import lexeme.java.tree.expression.statement.NewInstance;

public final class NewInstanceSizer extends SyntaxSizer<NewInstance> {
	public static final NewInstanceSizer NEW_INSTANCE_SIZER = new NewInstanceSizer();

	@Override
	public int size(NewInstance obj) {
		int classNameSize = ClassNameSizer.CLASS_NAME_SIZER.size(obj.getClassName());
		int paramsSize = obj.getConstructorArguments().isPresent() ? StatementSizer.STATEMENT_SIZER.size(obj.getConstructorArguments().get().getParameters())
			: 0;
		int arraySize = obj.getArrayDeclaration().isPresent()
			? ArrayInitializationSizer.ARRAY_INITIALIZATION_SIZER.size(obj.getArrayDeclaration().get().getInit())
			: 0;
		return classNameSize + paramsSize + arraySize + 1;
	}
}
