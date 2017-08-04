package parser.syntaxtree.expression.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.tokens.Bracket;

/**
 * Represents an array access such as array[i].
 */
@Getter
@AllArgsConstructor
public class ArrayAccess extends Statement {

    private final Statement source;
    private final Statement index;

    /**
     * Attempts to build an array access on a source statement, fabricated with the input text.
     * @param source the source statement (such as variable reference or method result)
     * @param input the mutable input text (is modified if an object was created
     * @return Optionally, an {@link ArrayAccess}
     */
    public static Optional<ArrayAccess> build(Statement source, AtomicReference<String> input) {
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(input.get());
        if (!Bracket.open(defensiveCopy)) {
            return Optional.empty();
        }

        Optional<? extends Statement> index = Statement.build(defensiveCopy);
        if (!index.isPresent()) {
            return Optional.empty();
        }

        if (!Bracket.close(defensiveCopy)) {
            return Optional.empty();
        }
        // An array access has been found !
        ArrayAccess access = new ArrayAccess(source, index.get());

        // Attempt to chain array access in other dimensions
        Optional<ArrayAccess> furtherDimension = ArrayAccess.build(access, defensiveCopy);
        if (furtherDimension.isPresent()) {
            access = furtherDimension.get();
        }

        // Commit
        input.set(defensiveCopy.get());
        return Optional.of(access);
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public List<String> show(String prefix) {
        final List<String> indexShow = index.show("");
        if (indexShow.size() == 0) {
            return Arrays.asList(prefix + "[" + indexShow + "]");
        } else {
            List<String> result = new ArrayList<>();
            result.add(prefix + "[");
            for (int i = 0; i < indexShow.size(); i++) {
                result.add(prefix + "  " + indexShow.get(i));
            }
            result.add(prefix + "]");
            return result;
        }
    }

    @Override
    public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
