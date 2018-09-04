package lexeme.java.tree.expression.statement;

import java.util.Optional;

import lexeme.java.intervals.Parenthesis;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.ExpressionVisitor;
import lexeme.java.tree.expression.statement.operators.binary.BinaryOperator;
import lexeme.java.tree.expression.statement.operators.unary.prefix.PrefixUnaryOperator;
import lexeme.java.tree.expression.statement.primitivetypes.PrimitiveValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;

/**
 * Represents a statement in the body of a method. Those represent entities, so can be chained with ".", be assigned or passed.
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
    public static Optional<? extends Statement> build(CodeBranch input) {// Try parenthesis grouping
        CodeBranch fork = input.fork();
        // Try a prefix operator
        Optional<? extends Statement> prefix = PrefixUnaryOperator.buildUnaryPrefix(fork);
        if (prefix.isPresent()) {
            return prefix;
        }

        // Try a lone statement
        Optional<? extends Statement> simple = buildSimple(fork);
        if (!simple.isPresent()) {
            return Optional.empty();
        }

        // A minimal Statement has been found!

        // Chain [field access / method access / array access] greedily
        Optional<? extends Statement> chain = attemptChain(simple.get(), fork);
        if (!chain.isPresent()) {
            chain = simple; // no chain, just use the simple statement
        }

        // Try a binary operator
        Optional<? extends BinaryOperator> binary = BinaryOperator.build(chain.get(), fork);
        fork.commit(); // commit regardless
        if (binary.isPresent()) {
            return binary;
        } else {
            return chain;
        }
    }


    /**
     * Attempts to chain statements with ".".
     * @param source the subject of the "." operator
     * @param input the input text (will be mutated if object is built)
     * @return optionally, an {@link ChainedAccess}
     */
    public static Optional<? extends Statement> attemptChain(Statement source, CodeBranch input) {
        // Whatever the simple statement is, it may be chained ?
        Optional<ChainedAccess> chain = ChainedAccess.build(source, input);
        if (chain.isPresent()) {
            return chain;
        }
        Optional<ArrayAccess> arrayAccess = ArrayAccess.build(source, input); // TODO why test for arrays ?
        if (arrayAccess.isPresent()) {
            return attemptChain(arrayAccess.get(), input);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<? extends Statement> buildSimple(CodeBranch input) {
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
