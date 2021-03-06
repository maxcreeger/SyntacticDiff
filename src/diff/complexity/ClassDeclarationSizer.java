package diff.complexity;

import diff.complexity.expression.VariableDeclarationSizer;
import lexeme.java.tree.ClassDeclaration;

public final class ClassDeclarationSizer extends SyntaxSizer<ClassDeclaration> {
    public static final ClassDeclarationSizer CLASS_DECLARATION_SIZER = new ClassDeclarationSizer();

    @Override
    public int size(ClassDeclaration obj) {
        int classComplexity = ClassNameSizer.CLASS_NAME_SIZER.size(obj.getClassName());
        int fieldsComplexity = VariableDeclarationSizer.VARIABLE_DECLARATION_SIZER.size(obj.getFields());
        int innerComplexity = ClassDeclarationSizer.CLASS_DECLARATION_SIZER.size(obj.getInnerClasses());
		int constructorComplexity = ConstructorSizer.CONSTRUCTOR_SIZER.size(obj.getConstructors()); 
        int methodsComplexity = MethodDeclarationSizer.METHOD_DECLARATION_SIZER.size(obj.getMethods());
        int qualifiersComplexity = obj.getQualifiers().size();
        int staticfieldsComplexity = VariableDeclarationSizer.VARIABLE_DECLARATION_SIZER.size(obj.getStaticFields());
        int staticInnerComplexity = ClassDeclarationSizer.CLASS_DECLARATION_SIZER.size(obj.getStaticInnerClasses());
		return classComplexity + constructorComplexity + fieldsComplexity + innerComplexity + methodsComplexity + qualifiersComplexity + staticfieldsComplexity
                + staticInnerComplexity;
    }
}
