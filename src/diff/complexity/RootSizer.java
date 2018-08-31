package diff.complexity;

import lexeme.java.tree.Root;

public final class RootSizer extends SyntaxSizer<Root> {

    public static final RootSizer ROOT_SIZER = new RootSizer();

    @Override
    public int size(Root obj) {
        int packageSize = 1;
        int importsCount = obj.getImports().size();
        int classComplexity = ClassDeclarationSizer.CLASS_DECLARATION_SIZER.size(obj.getClassDeclaration());
        return packageSize + importsCount + classComplexity;
    }
}
