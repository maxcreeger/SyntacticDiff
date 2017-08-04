package parser.syntaxtree.expression.statement;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.ExpressionVisitor;
import parser.syntaxtree.expression.statement.operators.binary.BinaryOperator;
import parser.syntaxtree.expression.statement.operators.unary.prefix.PrefixUnaryOperator;
import parser.syntaxtree.expression.statement.primitivetypes.PrimitiveValue;
import parser.tokens.Parenthesis;

/**
 * Represents a statement in the body of a method. Those can be chained with ".".
 */
@Getter
@AllArgsConstructor
public abstract class Statement extends Expression {

    /**
     * Tells if this statement can receive a value.
     * @return true if it can
     */
    public abstract boolean isAssignable();

    /**
     * Attempts to build a Statement.
     * @param input the input text (will be mutated if object is built)
     * @return optionally, a Statement
     */
    public static Optional<? extends Statement> build(AtomicReference<String> input) {// Try parenthesis grouping
        // Try a prefix operator
        Optional<? extends Statement> prefix = PrefixUnaryOperator.buildUnaryPrefix(input);
        if (prefix.isPresent()) {
            return prefix;
        }

        // Try a lone statement
        Optional<? extends Statement> simple = buildSimple(input);
        if (!simple.isPresent()) {
            return Optional.empty();
        }

        // A minimal Statement has been found!

        // Chain [field access / method access / array access] greedily
        simple = attemptChain(simple.get(), input);

        // Try a binary operator
        Optional<? extends BinaryOperator> binary = BinaryOperator.build(simple.get(), input);
        if (binary.isPresent()) {
            return binary;
        }

        return simple;
    }


    /**
     * Attempts to chain statements with ".".
     * @param source the subject of the "." operator
     * @param input the input text (will be mutated if object is built)
     * @return optionally, an {@link ChainedAccess}
     */
    public static Optional<? extends Statement> attemptChain(Statement source, AtomicReference<String> input) {
        // Whatever the simple statement is, it may be chained ?
        Optional<ChainedAccess> chain = ChainedAccess.build(source, input);
        if (chain.isPresent()) {
            return attemptChain(chain.get(), input);
        }
        Optional<ArrayAccess> arrayAccess = ArrayAccess.build(source, input);
        if (arrayAccess.isPresent()) {
            return attemptChain(arrayAccess.get(), input);
        } else {
            return Optional.of(source);
        }
    }

    private static Optional<? extends Statement> buildSimple(AtomicReference<String> input) {
        if (Parenthesis.open(input)) {
            // Statement in parenthesis
            Optional<? extends Statement> statement = Statement.build(input);
            Parenthesis.close(input);
            return statement;
        }

        // Try a base type value (int, string, float...)
        Optional<? extends Statement> optional = PrimitiveValue.build(input);
        if (optional.isPresent()) {
            return optional;
        }

        // Try a new Instance()
        optional = NewInstance.build(input);
        if (optional.isPresent()) {
            return optional;
        }

        // Try a reference to this or to super
        optional = SelfReference.build(input);
        if (optional.isPresent()) {
            return optional;
        }

        // Try straight method invocation
        optional = MethodInvocation.build(input);
        if (optional.isPresent()) {
            return optional;
        }

        // Try variable reference
        optional = VariableReference.build(input);
        if (optional.isPresent()) {
            return optional;
        }

        // Nothing found
        return Optional.empty();
    }

    @Override
    public <T> T acceptExpressionVisitor(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Accept a {@link StatementVisitor}.
     * @param <T> the type of the returned object
     * @param visitor the visitor
     * @return the returned object
     */
    public abstract <T> T acceptStatementVisitor(StatementVisitor<T> visitor);

}
