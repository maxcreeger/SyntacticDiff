package diff.similarity.evaluator;

import java.util.ArrayList;
import java.util.List;

import diff.complexity.ClassNameSizer;
import diff.similarity.LeafSimilarity;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.NoSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import lexeme.java.tree.ClassName;
import lexeme.java.tree.ClassName.ArrayDimension;

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
            dimSimilarity = new LeafSimilarity<ArrayDimension>(0, 1, class1.getArrayDimension(), class2.getArrayDimension()) {

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
        if (class1.getSuperClass().isPresent()) {
            if (class2.getSuperClass().isPresent()) {
                superSimilarity = this.eval(class1.getSuperClass().get(), class2.getSuperClass().get());
            } else {
                superSimilarity = new LeftLeafSimilarity<>(sizer.size(class1.getSuperClass().get()), class1.getSuperClass().get());
            }
        } else {
            if (class2.getSuperClass().isPresent()) {
                superSimilarity = new RightLeafSimilarity<>(sizer.size(class2.getSuperClass().get()), class2.getSuperClass().get());
            } else {
                superSimilarity = new NoSimilarity();
            }
        }
        // 'Extends' keyword
        final Similarity extendsSimilarity;
        if (class1.getExtendsClass().isPresent()) {
            if (class2.getExtendsClass().isPresent()) {
                extendsSimilarity = this.eval(class1.getExtendsClass(), class2.getExtendsClass());
            } else {
                extendsSimilarity = new LeftLeafSimilarity<>(sizer.size(class1.getExtendsClass().get()), class1.getExtendsClass().get());
            }
        } else {
            if (class2.getExtendsClass().isPresent()) {
                extendsSimilarity = new RightLeafSimilarity<>(sizer.size(class2.getExtendsClass().get()), class2.getExtendsClass().get());
            } else {
                extendsSimilarity = new NoSimilarity();
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
