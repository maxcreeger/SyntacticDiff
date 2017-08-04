package diff.similarity.evaluator;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.ClassNameSizer;
import diff.similarity.LeafSimilarity;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.NoSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import parser.syntaxtree.ClassName;

/**
 * Compares two {@link ClassName}s.
 */
public class ClassNameSimilarityEvaluator extends SimilarityEvaluator<ClassName> {

    /** Instance. */
    public static final ClassNameSimilarityEvaluator INSTANCE = new ClassNameSimilarityEvaluator();

    private ClassNameSimilarityEvaluator() {
        super(ClassNameSizer.CLASS_NAME_SIZER, "classname");
    }

    public Similarity eval(ClassName class1, ClassName class2) {
        // Dimensions
        final Similarity dimSimilarity;
        if (class1.getArrayDimension() == class2.getArrayDimension()) {
            dimSimilarity = new NoSimilarity();
        } else {
            dimSimilarity = new LeafSimilarity<Integer>(0, 1, class1.getArrayDimension(), class2.getArrayDimension()) {

                @Override
                public List<String[]> show(String prefix) {
                    List<String[]> list = new ArrayList<>();
                    list.add(new String[] {prefix + class1, "0", prefix + class2});
                    return list;
                }
            };
        }

        // 'Super' keyword
        final Similarity superSimilarity;
        if (class1.getSuperClass() == null) {
            if (class2.getSuperClass() == null) {
                superSimilarity = new NoSimilarity();
            } else {
                superSimilarity = new RightLeafSimilarity<>(sizer.size(class2.getSuperClass()), class2.getSuperClass());
            }
        } else {
            if (class2.getSuperClass() == null) {
                superSimilarity = new LeftLeafSimilarity<>(sizer.size(class1.getSuperClass()), class1.getSuperClass());
            } else {
                superSimilarity = this.eval(class1.getSuperClass(), class2.getSuperClass());
            }
        }
        // 'Extends' keyword
        final Similarity extendsSimilarity;
        if (class1.getExtendsClass() == null) {
            if (class2.getExtendsClass() == null) {
                extendsSimilarity = new NoSimilarity();
            } else {
                extendsSimilarity = new RightLeafSimilarity<>(sizer.size(class2.getExtendsClass()), class2.getExtendsClass());
            }
        } else {
            if (class2.getExtendsClass() == null) {
                extendsSimilarity = new LeftLeafSimilarity<>(sizer.size(class1.getExtendsClass()), class1.getExtendsClass());
            } else {
                extendsSimilarity = this.eval(class1.getExtendsClass(), class2.getExtendsClass());
            }
        }
        // Interfaces
        Similarity interfacesSimilarity = this.maximumMatch(class1.getImplementedInterfaces(), class2.getImplementedInterfaces());
        // Naming
        Similarity nameSimilarity = Similarity.eval(class1.getName(), class2.getName());
        // Sub parameters (Generic type arguments)
        Similarity genericSimilarity;
        if (class1.getNestedSubParameters() == null) {
            if (class2.getNestedSubParameters() == null) {
                genericSimilarity = new NoSimilarity();
            } else {
                genericSimilarity = RightLeafSimilarity.build(ClassNameSizer.CLASS_NAME_SIZER.size(class2.getNestedSubParameters()),
                        class2.getNestedSubParameters());
            }
        } else {
            if (class2.getNestedSubParameters() == null) {
                genericSimilarity = LeftLeafSimilarity.build(ClassNameSizer.CLASS_NAME_SIZER.size(class1.getNestedSubParameters()),
                        class1.getNestedSubParameters());
            } else {
                genericSimilarity = this.orderedEval(class1.getNestedSubParameters(), class2.getNestedSubParameters());
            }
        }
        // Compile all (LOL compile!)
        return Similarity.add(name, dimSimilarity, superSimilarity, extendsSimilarity, interfacesSimilarity, nameSimilarity, genericSimilarity);
    }
}
