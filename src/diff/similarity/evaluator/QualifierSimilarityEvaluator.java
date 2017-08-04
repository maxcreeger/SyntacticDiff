package diff.similarity.evaluator;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.QualifierSizer;
import diff.similarity.LeafSimilarity;
import diff.similarity.Similarity;
import parser.syntaxtree.Qualifiers;

/**
 * Compares two {@link Qualifiers}s.
 */

public class QualifierSimilarityEvaluator extends SimilarityEvaluator<Qualifiers> {

    /** Evaluates {@link Qualifiers}. */
    public static final QualifierSimilarityEvaluator INSTANCE = new QualifierSimilarityEvaluator();

    private QualifierSimilarityEvaluator() {
        super(QualifierSizer.QUALIFIER_SIZER, "qualy");
    }

    public Similarity eval(Qualifiers qualifier1, Qualifiers qualifier2) {
        return new LeafSimilarity<Qualifiers>(qualifier1.equals(qualifier2) ? 1 : 0, 1, qualifier1, qualifier2) {

            @Override
            public List<String[]> show(String prefix) {
                List<String[]> list = new ArrayList<>();
                list.add(new String[] {prefix + qualifier1, Double.toString(similarity()), prefix + qualifier2});
                return list;
            }
        };
    }
}
