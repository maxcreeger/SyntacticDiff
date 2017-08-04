package parser.syntaxtree.expression.statement;

import parser.syntaxtree.expression.statement.operators.Operator;
import parser.syntaxtree.expression.statement.primitivetypes.PrimitiveValue;

public interface StatementVisitor<T> {

    T visit(VariableReference variableReference);

    T visit(SelfReference selfReference);

    T visit(Return return1);

    T visit(NewInstance newInstance);

    T visit(MethodInvocation methodInvocation);

    T visit(ChainedAccess chainedAccess);

    T visit(ArrayDeclaration arrayDeclaration);

    T visit(PrimitiveValue primitiveValue);

    T visit(ArrayAccess arrayAccess);

    T visit(Operator operator);

}
