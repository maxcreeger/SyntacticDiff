package diff.complexity.expression.statement;

import diff.complexity.SyntaxSizer;
import parser.syntaxtree.expression.statement.ArrayDeclaration.ArrayInitialisationLeaf;
import parser.syntaxtree.expression.statement.ArrayDeclaration.ArrayInitialisationRec;
import parser.syntaxtree.expression.statement.ArrayDeclaration.ArrayInitialization;
import parser.syntaxtree.expression.statement.ArrayDeclaration.ArrayInitializationVisitor;
import parser.syntaxtree.expression.statement.ArrayDeclaration.ArraySizeDeclaration;

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
