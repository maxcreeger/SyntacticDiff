package parser.syntaxtree.expression.statement.operators.binary;

/**
 * {@link BinaryOperator} visitor.
 *
 * @param <T> the returned object type
 */
public interface BinaryOperatorVisitor<T> {

    /**
     * Visits an {@link Addition} operator.
     * @param addition the visited operator
     * @return the result
     */
    T visit(Addition addition);

    /**
     * Visits an {@link Assignment} operator.
     * @param assignment the visited operator
     * @return the result
     */
    T visit(Assignment assignment);

    /**
     * Visits an {@link BooleanAnd} operator.
     * @param booleanAnd the visited operator
     * @return the result
     */
    T visit(BooleanAnd booleanAnd);

    /**
     * Visits an {@link BooleanOr} operator.
     * @param booleanOr the visited operator
     * @return the result
     */
    T visit(BooleanOr booleanOr);

    /**
     * Visits an {@link Different} operator.
     * @param different the visited operator
     * @return the result
     */
    T visit(Different different);

    /**
     * Visits an {@link Division} operator.
     * @param division the visited operator
     * @return the result
     */
    T visit(Division division);

    /**
     * Visits an {@link Equals} operator.
     * @param equals the visited operator
     * @return the result
     */
    T visit(Equals equals);

    /**
     * Visits an {@link Multiply} operator.
     * @param multiply the visited operator
     * @return the result
     */
    T visit(Multiply multiply);

    /**
     * Visits an {@link Subtraction} operator.
     * @param substraction the visited operator
     * @return the result
     */
    T visit(Subtraction substraction);
}
