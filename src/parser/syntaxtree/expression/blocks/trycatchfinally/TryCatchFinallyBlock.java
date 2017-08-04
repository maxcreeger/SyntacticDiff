package parser.syntaxtree.expression.blocks.trycatchfinally;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.blocks.AbstractBlock;
import parser.syntaxtree.expression.blocks.BlockVisitor;

/**
 * Represents a Try { body }. <br>
 * May have resources in the try, a catch statement, and a finally statement.
 */
@Getter
@AllArgsConstructor
public class TryCatchFinallyBlock extends AbstractBlock {

    private final TryBlock tryBlock;
    private final List<CatchBlock> catchBlocks;
    private Optional<FinallyBlock> finallyBlock;

    /**
     * Attempts to build a {@link TryCatchFinallyBlock}
     * @param inputRef the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static Optional<TryCatchFinallyBlock> build(AtomicReference<String> inputRef) {
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(inputRef.get());

        // Try
        Optional<TryBlock> tryBlock = TryBlock.build(defensiveCopy);
        if (!tryBlock.isPresent()) {
            return Optional.empty();
        }

        // Catch
        List<CatchBlock> catches = CatchBlock.build(defensiveCopy);

        // Finally
        Optional<FinallyBlock> finallyBlock = FinallyBlock.build(defensiveCopy);

        // Sanity check
        if (tryBlock.get().getTryWithResources().isEmpty() && catches.isEmpty() && !finallyBlock.isPresent()) {
            // Meaningless try: no resource, no catch, no finally. WTF?
            return Optional.empty();
        }

        // Commit
        inputRef.set(defensiveCopy.get());
        // System.out.println("try[catch][finally] block detected");
        return Optional.of(new TryCatchFinallyBlock(tryBlock.get(), catches, finallyBlock));
    }

    @Override
    public <T> T acceptBlockVisitor(BlockVisitor<T> visitor) {
        return visitor.visit(this);
    }

    // Display

    @Override
    public List<String> show(String prefix) {
        List<String> total = new ArrayList<>();

        // TRY BLOCK
        total.addAll(tryBlock.show(prefix));

        // CATCH BLOCK(S)
        for (CatchBlock catchBlock : catchBlocks) {
            total.addAll(catchBlock.show(prefix));
        }
        // FINALLY
        if (finallyBlock.isPresent()) {
            total.addAll(finallyBlock.get().show(prefix));
        }

        return total;
    }

    @Override
    public List<Expression> getBody() {
        return tryBlock.getTryExpressions();
    }

}
