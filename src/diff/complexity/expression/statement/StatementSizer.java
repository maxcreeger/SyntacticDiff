package diff.complexity.expression.statement;

import diff.complexity.ClassNameSizer;
import diff.complexity.SyntaxSizer;
import diff.complexity.expression.statement.operator.OperatorSizer;
import diff.complexity.expression.statement.primitivetypes.PrimitiveValueSizer;
import lexeme.java.tree.expression.statement.ArrayAccess;
import lexeme.java.tree.expression.statement.ArrayDeclaration;
import lexeme.java.tree.expression.statement.ChainedAccess;
import lexeme.java.tree.expression.statement.MethodInvocation;
import lexeme.java.tree.expression.statement.NewInstance;
import lexeme.java.tree.expression.statement.Return;
import lexeme.java.tree.expression.statement.SelfReference;
import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.StatementVisitor;
import lexeme.java.tree.expression.statement.VariableReference;
import lexeme.java.tree.expression.statement.operators.Operator;
import lexeme.java.tree.expression.statement.primitivetypes.PrimitiveValue;

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
        int total = this.size(chainedAccess.getStatements());
        return chainedAccess.getStatements().size() + total;
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
