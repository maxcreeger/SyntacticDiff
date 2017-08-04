package parser.syntaxtree.expression.statement.primitivetypes;

/**
 * visitor of a {@link PrimitiveValue}
 *
 * @param <T> the outcome
 */
public interface PrimitiveVisitor<T> {

    /**
     * Visits a {@link StringValue}.
     * @param stringValue the visited object
     * @return the outcome of the visit
     */
    T visit(StringValue stringValue);

    /**
     * Visits a {@link NullValue}.
     * @param nullValue the visited object
     * @return the outcome of the visit
     */
    T visit(NullValue nullValue);

    /**
     * Visits a {@link IntegerValue}.
     * @param integerValue the visited object
     * @return the outcome of the visit
     */
    T visit(IntegerValue integerValue);

    /**
     * Visits a {@link DoubleValue}.
     * @param doubleValue the visited object
     * @return the outcome of the visit
     */
    T visit(DoubleValue doubleValue);

    /**
     * Visits a {@link CharValue}.
     * @param charValue the visited object
     * @return the outcome of the visit
     */
    T visit(CharValue charValue);

    /**
     * Visits a {@link BooleanValue}.
     * @param booleanValue the visited object
     * @return the outcome of the visit
     */
    T visit(BooleanValue booleanValue);

}
