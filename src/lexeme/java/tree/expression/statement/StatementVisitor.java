package lexeme.java.tree.expression.statement;

import lexeme.java.tree.expression.statement.operators.Operator;
import lexeme.java.tree.expression.statement.primitivetypes.PrimitiveValue;

/**
 * Visitor for {@link Statement}s
 *
 * @param <T> a return type
 */
public interface StatementVisitor<T> {

    /**
     * Visit a {@link VariableReference}
     * @param variableReference
     * @return a result
     */
    T visit(VariableReference variableReference);

    /**
     * Visit a {@link SelfReference}
     * @param selfReference
     * @return a result
     */
    T visit(SelfReference selfReference);

    /**
     * Visit a {@link Return}
     * @param return1
     * @return a result
     */
    T visit(Return return1);

    /**
     * Visit a {@link NewInstance}
     * @param newInstance
     * @return a result
     */
    T visit(NewInstance newInstance);

    /**
     * Visit a {@link MethodInvocation}
     * @param methodInvocation
     * @return a result
     */
    T visit(MethodInvocation methodInvocation);

    /**
     * Visit a {@link ChainedAccess}
     * @param chainedAccess
     * @return a result
     */
    T visit(ChainedAccess chainedAccess);

    /**
     * Visit a {@link ArrayDeclaration}
     * @param arrayDeclaration
     * @return a result
     */
    T visit(ArrayDeclaration arrayDeclaration);

    /**
     * Visit a {@link PrimitiveValue}
     * @param primitiveValue
     * @return a result
     */
    T visit(PrimitiveValue primitiveValue);

    /**
     * Visit a {@link ArrayAccess}
     * @param arrayAccess
     * @return a result
     */
    T visit(ArrayAccess arrayAccess);

    /**
     * Visit a {@link Operator}
     * @param operator
     * @return a result
     */
    T visit(Operator operator);

}
