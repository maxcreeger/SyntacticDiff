package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lexeme.java.intervals.Bracket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents an array access such as array[i].
 */
@Getter
@AllArgsConstructor
public class ArrayAccess extends Statement {

    private final Statement source;
    private final Statement index;
    private final CodeLocation location;

    /**
     * Attempts to build an array access on a source statement, fabricated with the input text.
     * @param source the source statement (such as variable reference or method result)
     * @param input the mutable input text (is modified if an object was created
     * @return Optionally, an {@link ArrayAccess}
     */
    public static Optional<ArrayAccess> build(Statement source, CodeBranch input) {
        CodeBranch defensiveCopy = input.fork();
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
        // An array access has been found ! Commit!
        ArrayAccess access = new ArrayAccess(source, index.get(), defensiveCopy.commit());

        // Attempt to chain array access in other dimensions
        defensiveCopy = input.fork(); // Fork again for recursive pass
        Optional<ArrayAccess> furtherDimension = ArrayAccess.build(access, defensiveCopy);
        if (furtherDimension.isPresent()) {
            access = furtherDimension.get();
        }

        // Return
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
