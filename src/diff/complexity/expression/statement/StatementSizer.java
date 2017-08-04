package diff.complexity.expression.statement;

import diff.complexity.ClassNameSizer;
import diff.complexity.SyntaxSizer;
import diff.complexity.expression.statement.operator.OperatorSizer;
import diff.complexity.expression.statement.primitivetypes.PrimitiveValueSizer;
import parser.syntaxtree.expression.statement.ArrayAccess;
import parser.syntaxtree.expression.statement.ArrayDeclaration;
import parser.syntaxtree.expression.statement.ChainedAccess;
import parser.syntaxtree.expression.statement.MethodInvocation;
import parser.syntaxtree.expression.statement.NewInstance;
import parser.syntaxtree.expression.statement.Return;
import parser.syntaxtree.expression.statement.SelfReference;
import parser.syntaxtree.expression.statement.Statement;
import parser.syntaxtree.expression.statement.StatementVisitor;
import parser.syntaxtree.expression.statement.VariableReference;
import parser.syntaxtree.expression.statement.operators.Operator;
import parser.syntaxtree.expression.statement.primitivetypes.PrimitiveValue;

public final class StatementSizer extends SyntaxSizer<Statement> implements StatementVisitor<Integer> {
    public static final StatementSizer STATEMENT_SIZER = new StatementSizer();

    @Override
    public int size(Statement obj) {
        return obj.acceptStatementVisitor(this);
    }

    @Override
    public Integer visit(VariableReference variableReference) {
        return VariableReferenceSizer.VARIABLE_REFERENCE_SIZER.size(variableReference);
    }

    @Override
    public Integer visit(SelfReference selfReference) {
        return SelfReferenceSizer.SELF_REFERENCE_SIZER.size(selfReference);
    }

    @Override
    public Integer visit(Return return1) {
        return ReturnSizer.RETURN_SIZER.size(return1);
    }

    @Override
    public Integer visit(NewInstance newInstance) {
        return NewInstanceSizer.NEW_INSTANCE_SIZER.size(newInstance);
    }

    @Override
    public Integer visit(MethodInvocation methodInvocation) {
        return MethodInvocationSizer.METHOD_INVOCATION_SIZER.size(methodInvocation);
    }

    @Override
    public Integer visit(ChainedAccess chainedAccess) {
        int initialStatement = this.size(chainedAccess.getSource());
        int chain = this.size(chainedAccess.getChainedAction());
        return initialStatement + 1 + chain;
    }

    @Override
    public Integer visit(ArrayDeclaration arrayDeclaration) {
        int typeComplexity = ClassNameSizer.CLASS_NAME_SIZER.size(arrayDeclaration.getClassName());
        int initialization = ArrayInitializationSizer.ARRAY_INITIALIZATION_SIZER.size(arrayDeclaration.getInit());
        return typeComplexity + initialization;
    }

    @Override
    public Integer visit(PrimitiveValue primitiveValue) {
        return PrimitiveValueSizer.PRIMITIVE_VALUE_SIZER.size(primitiveValue);
    }

    @Override
    public Integer visit(ArrayAccess arrayAccess) {
        int sourceComplexity = this.size(arrayAccess.getSource());
        int arrayIndexComplexity = this.size(arrayAccess.getIndex());
        return sourceComplexity + 1 + arrayIndexComplexity;
    }

    @Override
    public Integer visit(Operator operator) {
        return OperatorSizer.OPERATOR_SIZER.size(operator);
    }

}
