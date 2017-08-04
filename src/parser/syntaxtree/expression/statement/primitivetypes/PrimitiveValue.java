package parser.syntaxtree.expression.statement.primitivetypes;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.expression.statement.Statement;
import parser.syntaxtree.expression.statement.StatementVisitor;

/**
 * Represents a primitive value in the code such as <code>true</code> or <code>0.45f</code>
 */
@Getter
@AllArgsConstructor
public abstract class PrimitiveValue extends Statement {

    /**
     * Attempts to build any primitive.
     * @param inputRef the mutable input text (is modified if the primitive is created)
     * @return optionally, the primitive
     */
    public static Optional<? extends PrimitiveValue> build(AtomicReference<String> inputRef) {
        Optional<? extends PrimitiveValue> opt = BooleanValue.build(inputRef);
        if (opt.isPresent()) {
            return opt;
        }
        opt = NullValue.build(inputRef);
        if (opt.isPresent()) {
            return opt;
        }
        opt = IntegerValue.build(inputRef);
        if (opt.isPresent()) {
            return opt;
        }
        opt = DoubleValue.build(inputRef);
        if (opt.isPresent()) {
            return opt;
        }
        opt = CharValue.build(inputRef);
        if (opt.isPresent()) {
            return opt;
        }
        opt = StringValue.build(inputRef);
        if (opt.isPresent()) {
            return opt;
        }
        return Optional.empty();
    }

    @Override
    public boolean isAssignable() {
        return false;
    }

    @Override
    public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Accepts a visitor.
     * @param <T> the outcome of the visit
     * @param visitor the visitor
     * @return the outcome
     */
    public abstract <T> T visit(PrimitiveVisitor<T> visitor);

}
