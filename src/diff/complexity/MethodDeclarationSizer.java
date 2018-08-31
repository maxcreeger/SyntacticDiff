package diff.complexity;

import diff.complexity.expression.ExpressionSizer;
import lexeme.java.tree.MethodDeclaration;

public final class MethodDeclarationSizer extends SyntaxSizer<MethodDeclaration> {
    public static final MethodDeclarationSizer METHOD_DECLARATION_SIZER = new MethodDeclarationSizer();

    @Override
    public int size(MethodDeclaration method) {
        int returnTypeComplexity = ClassNameSizer.CLASS_NAME_SIZER.size(method.getReturnType());
        int qualifiersCount = method.getQualifiers().size();
        int nameSize = 1; // For
                          // name
        int parametersComplexity = ParameterTypeDeclarationSizer.PARAMETER_TYPE__SIZER.size(method.getParameters());
        int expressionComplexity = ExpressionSizer.EXPRESSION_SIZER.size(method.getExpressions());
        return returnTypeComplexity + qualifiersCount + nameSize + parametersComplexity + expressionComplexity;
    }
}
