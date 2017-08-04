package diff.similarity.evaluator;

import diff.complexity.ClassDeclarationSizer;
import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.VariableDeclarationSimilarityEvaluator;
import parser.syntaxtree.ClassDeclaration;

/**
 * Compares two {@link ClassDeclaration}s.
 */
public class ClassDeclarationSimilarityEvaluator extends SimilarityEvaluator<ClassDeclaration> {
    /** Instance. */
    public static final ClassDeclarationSimilarityEvaluator INSTANCE = new ClassDeclarationSimilarityEvaluator();

    private ClassDeclarationSimilarityEvaluator() {
        super(ClassDeclarationSizer.CLASS_DECLARATION_SIZER, "class");
    }

    public Similarity eval(ClassDeclaration class1, ClassDeclaration class2) {
        Similarity qualifiersSim = QualifierSimilarityEvaluator.INSTANCE.maximumMatch(class1.getQualifiers(), class2.getQualifiers());
        Similarity classNameSim = ClassNameSimilarityEvaluator.INSTANCE.eval(class1.getClassName(), class2.getClassName());
        Similarity fieldsSim = VariableDeclarationSimilarityEvaluator.INSTANCE.maximumMatch(class1.getFields(), class2.getFields());
        Similarity innerClassDeclarationSim =
                ClassDeclarationSimilarityEvaluator.INSTANCE.maximumMatch(class1.getInnerClasses(), class2.getInnerClasses());
        Similarity methodsSim = MethodDeclarationSimilarityEvaluator.INSTANCE.maximumMatch(class1.getMethods(), class2.getMethods());
        Similarity staticFieldsSim = VariableDeclarationSimilarityEvaluator.INSTANCE.maximumMatch(class1.getStaticFields(), class2.getStaticFields());
        Similarity staticInnerClassDeclarationSim =
                ClassDeclarationSimilarityEvaluator.INSTANCE.maximumMatch(class1.getStaticInnerClasses(), class2.getStaticInnerClasses());
        Similarity staticMethodsSim =
                MethodDeclarationSimilarityEvaluator.INSTANCE.maximumMatch(class1.getStaticMethods(), class2.getStaticMethods());
        return Similarity.add("class", qualifiersSim, classNameSim, fieldsSim, innerClassDeclarationSim, methodsSim, staticFieldsSim,
                staticInnerClassDeclarationSim, staticMethodsSim);
    }
}
