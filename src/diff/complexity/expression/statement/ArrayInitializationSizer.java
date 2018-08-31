package diff.complexity.expression.statement;

import diff.complexity.SyntaxSizer;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitialisationLeaf;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitialisationRec;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitialization;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArrayInitializationVisitor;
import lexeme.java.tree.expression.statement.ArrayDeclaration.ArraySizeDeclaration;

public final class ArrayInitializationSizer extends SyntaxSizer<ArrayInitialization> implements ArrayInitializationVisitor<Integer> {
    public static final ArrayInitializationSizer ARRAY_INITIALIZATION_SIZER = new ArrayInitializationSizer();

    @Override
    public int size(ArrayInitialization obj) {
        return obj.accept(this);
    }

    @Override
    public Integer visit(ArrayInitialisationLeaf leaf) {
        return StatementSizer.STATEMENT_SIZER.size(leaf.getValues()) + 1;
    }

    @Override
    public Integer visit(ArrayInitialisationRec rec) {
        return this.size(rec.getSubDimensions()) + 1;
    }

    @Override
    public Integer visit(ArraySizeDeclaration decla) {
        return decla.getDimensions().length;
    }

}
