package lexeme.java.tree.expression.blocks.trycatchfinally;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.blocks.BlockVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

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
    private final CodeLocation location;

    /**
     * Attempts to build a {@link TryCatchFinallyBlock}
     * @param inputRef the input text (is modified if the block is built)
     * @return optionally, the block
     */
    public static Optional<TryCatchFinallyBlock> build(CodeBranch inputRef) {
        CodeBranch fork = inputRef.fork();

        // Try
        Optional<TryBlock> tryBlock = TryBlock.build(fork);
        if (!tryBlock.isPresent()) {
            return Optional.empty();
        }

        // Catch
        List<CatchBlock> catches = CatchBlock.build(fork);

        // Finally
        Optional<FinallyBlock> finallyBlock = FinallyBlock.build(fork);

        // Sanity check
        if (tryBlock.get().getTryWithResources().isEmpty() && catches.isEmpty() && !finallyBlock.isPresent()) {
            // Meaningless try: no resource, no catch, no finally. WTF?
            return Optional.empty();
        }

        // Commit
        // System.out.println("try[catch][finally] block detected");
        return Optional.of(new TryCatchFinallyBlock(tryBlock.get(), catches, finallyBlock, fork.commit()));
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
