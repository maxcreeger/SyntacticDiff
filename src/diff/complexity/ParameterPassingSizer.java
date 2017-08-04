package diff.complexity;

import diff.complexity.expression.statement.StatementSizer;
import parser.syntaxtree.ParameterPassing;

public class ParameterPassingSizer extends SyntaxSizer<ParameterPassing> {

    public static final ParameterPassingSizer PARAMETERS_PASSING_SIZER = new ParameterPassingSizer();

    @Override
    public int size(ParameterPassing obj) {
        return StatementSizer.STATEMENT_SIZER.size(obj.getParameters());
    }
}
