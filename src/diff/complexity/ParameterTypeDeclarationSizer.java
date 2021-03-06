package diff.complexity;

import lexeme.java.tree.ParameterTypeDeclaration;

public final class ParameterTypeDeclarationSizer extends SyntaxSizer<ParameterTypeDeclaration> {
    public static final ParameterTypeDeclarationSizer PARAMETER_TYPE_SIZER = new ParameterTypeDeclarationSizer();

    @Override
    public int size(ParameterTypeDeclaration obj) {
        int qualSize = obj.getQualifiers().size();
        int nameSize = 1;
        int classNameSize = ClassNameSizer.CLASS_NAME_SIZER.size(obj.getType());
        return qualSize + nameSize + classNameSize;
    }
}
