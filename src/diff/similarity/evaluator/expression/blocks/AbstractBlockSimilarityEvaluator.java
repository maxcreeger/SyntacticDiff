package diff.similarity.evaluator.expression.blocks;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.expression.blocks.AbstractBlockSizer;
import diff.similarity.LeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import parser.syntaxtree.expression.blocks.AbstractBlock;

/**
 * Visitor of left-hand side {@link AbstractBlock}, that produces a specific visitor for the right-hand side {@link AbstractBlock} to produce a {@link Similarity}.
 */
public class AbstractBlockSimilarityEvaluator extends SimilarityEvaluator<AbstractBlock> {

    /** Instance */
    public static final AbstractBlockSimilarityEvaluator INSTANCE = new AbstractBlockSimilarityEvaluator();

    private AbstractBlockSimilarityEvaluator() {
        super(AbstractBlockSizer.ABSTRACT_BLOCK_SIZER, "block");
    }

    public Similarity eval(AbstractBlock bloc1, AbstractBlock bloc2) {
        // Try to match some blocks together (while / do / for / if)
        Similarity similarity = bloc2.acceptBlockVisitor(new TransgenderBlockSimilarityEvaluator(bloc1));
        if (similarity != null) { // Unimplemented methods may return null
            // Transgender comparison was made
            return similarity;
        } else {
            return new LeafSimilarity<AbstractBlock>(0., AbstractBlockSizer.ABSTRACT_BLOCK_SIZER.size(bloc1, bloc2), bloc1, bloc2) {
                @Override
                public List<String[]> show(String prefix) {
                    List<String> show1 = bloc1.show(prefix);
                    List<String> show2 = bloc2.show(prefix);
                    List<String[]> list = new ArrayList<>();
                    int i = 0;
                    int j = 0;
                    while (i < show1.size() && j < show2.size()) {
                        String left = i < show1.size() ? show1.get(i++) : "<------->";
                        String right = j < show2.size() ? show2.get(j++) : "<------->";
                        list.add(new String[] {left, "0", right});
                    }
                    return list;
                }
            };
        }
    }
}
