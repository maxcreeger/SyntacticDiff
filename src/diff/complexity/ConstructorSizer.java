package diff.complexity;

import lexeme.java.tree.Constructor;
import diff.complexity.expression.ExpressionSizer;

public class ConstructorSizer extends SyntaxSizer<Constructor> {

	public static final ConstructorSizer CONSTRUCTOR_SIZER = new ConstructorSizer();

	@Override
	public int size(Constructor method) {
		int qualifiersCount = method.getQualifiers().size();
		int nameSize = 1; // For
							// name 
		int parametersComplexity = ParameterTypeDeclarationSizer.PARAMETER_TYPE_SIZER.size(method.getParameters());
		int expressionComplexity = ExpressionSizer.EXPRESSION_SIZER.size(method.getExpressions());
		return qualifiersCount + nameSize + parametersComplexity + expressionComplexity;
	}

}
