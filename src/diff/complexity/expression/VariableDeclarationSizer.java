package diff.complexity.expression;

import diff.complexity.SyntaxSizer;
import diff.complexity.expression.statement.StatementSizer;
import lexeme.java.tree.expression.VariableDeclaration;

public final class VariableDeclarationSizer extends SyntaxSizer<VariableDeclaration> {
    public static final VariableDeclarationSizer VARIABLE_DECLARATION_SIZER = new VariableDeclarationSizer();

    @Override
    public int size(VariableDeclaration obj) {
        int qualifiersCount = obj.getQualifiers().size();
        int nameSize = 1;
        int assignmentcomplexity =
                obj.getInitialAssignement().isPresent() ? StatementSizer.STATEMENT_SIZER.size(obj.getInitialAssignement().get()) : 0;
        return qualifiersCount + nameSize + assignmentcomplexity;
    }
}
