package diff.similarity.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import diff.complexity.ClassDeclarationSizer;
import diff.complexity.ClassNameSizer;
import diff.similarity.CompositeSimilarity;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.NoSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.expression.VariableDeclarationSimilarityEvaluator;
import lexeme.java.tree.ClassDeclaration;
import lexeme.java.tree.ClassName;
import prettyprinting.SimilarityChainingHint;

/**
 * Compares two {@link ClassDeclaration}s.
 */
public class ClassDeclarationSimilarityEvaluator extends SimilarityEvaluator<ClassDeclaration> {
	/** Instance. */
	public static final ClassDeclarationSimilarityEvaluator INSTANCE = new ClassDeclarationSimilarityEvaluator();

	private ClassDeclarationSimilarityEvaluator() {
		super(ClassDeclarationSizer.CLASS_DECLARATION_SIZER, "class");
	}

	@Override
	public ClassDeclarationSimilarity eval(ClassDeclaration class1, ClassDeclaration class2) {
		List<Similarity> qualifiersSim = QualifierSimilarityEvaluator.INSTANCE.maximumMatchList(class1.getQualifiers(), class2.getQualifiers());
		Similarity nameSimilarity = Similarity.eval(class1.getClassName(), class2.getClassName());
		Similarity nestedSubParameters = subParamSimilarity(class1.getNestedSubParameters(), class2.getNestedSubParameters());
		Similarity extendsClass = ClassNameSimilarityEvaluator.INSTANCE.eval(class1.getExtendsClass(), class2.getExtendsClass());
		List<Similarity> implementedInterfaces = ClassNameSimilarityEvaluator.INSTANCE.maximumMatchList(class1.getImplementedInterfaces(),
			class2.getImplementedInterfaces());
		List<Similarity> fieldsSim = VariableDeclarationSimilarityEvaluator.INSTANCE.maximumMatchList(class1.getFields(), class2.getFields());
		List<Similarity> innerClassDeclarationSim = ClassDeclarationSimilarityEvaluator.INSTANCE.maximumMatchList(class1.getInnerClasses(),
			class2.getInnerClasses());
		List<Similarity> methodsSim = MethodDeclarationSimilarityEvaluator.INSTANCE.maximumMatchList(class1.getMethods(), class2.getMethods());
		List<Similarity> staticFieldsSim = VariableDeclarationSimilarityEvaluator.INSTANCE.maximumMatchList(class1.getStaticFields(), class2.getStaticFields());
		List<Similarity> staticInnerClassDeclarationSim = ClassDeclarationSimilarityEvaluator.INSTANCE.maximumMatchList(class1.getStaticInnerClasses(),
			class2.getStaticInnerClasses());
		List<Similarity> staticMethodsSim = MethodDeclarationSimilarityEvaluator.INSTANCE.maximumMatchList(class1.getStaticMethods(),
			class2.getStaticMethods());
		return ClassDeclarationSimilarity.build(qualifiersSim, nameSimilarity, nestedSubParameters, extendsClass, implementedInterfaces, fieldsSim,
			innerClassDeclarationSim, methodsSim, staticFieldsSim, staticInnerClassDeclarationSim, staticMethodsSim);
	}

	private Similarity subParamSimilarity(List<ClassName> listA, List<ClassName> listB) {
		if (listA == null) {
			// no parameters on left side
			if (listB == null) {
				return new NoSimilarity(); // no parameters either side
			} else {
				if (listB.isEmpty()) { // inferred parameters on right Side
					return new NoSimilarity(); // TODO wrong: must compare "" with "<>" --> needs a class to represent TypeParameters!
				} else { // B has types
					return Similarity.add("typeParam",
						listB.stream().map(cn -> new RightLeafSimilarity<>(ClassNameSizer.CLASS_NAME_SIZER.size(cn), cn)).collect(Collectors.toList()));
				}
			}
		} else {
			if (listA.isEmpty()) {
				// inferred parameters on left side
				if (listB == null) {
					return new NoSimilarity(); // TODO wrong: must compare "<>" with "" --> needs a class to represent TypeParameters!
				} else {
					if (listB.isEmpty()) { // inferred parameters on right Side
						return new NoSimilarity(); // TODO wrong: must compare "<>" with "<>" --> needs a class to represent TypeParameters!
					} else { // B has types
						return Similarity.add("typeParam",
							listB.stream().map(cn -> new RightLeafSimilarity<>(ClassNameSizer.CLASS_NAME_SIZER.size(cn), cn)).collect(Collectors.toList())); // TODO wrong: must compare "<>" with "<A,B...>" --> needs a class to represent TypeParameters!
					}
				}
			} else {
				// A has types
				if (listB == null) {
					return Similarity.add("typeParam",
						listA.stream().map(cn -> new LeftLeafSimilarity<>(ClassNameSizer.CLASS_NAME_SIZER.size(cn), cn)).collect(Collectors.toList())); // TODO wrong: must compare "<A,B...>" with "" --> needs a class to represent TypeParameters!				} else {
				} else {
					if (listB.isEmpty()) { // inferred parameters on right Side
						return Similarity.add("typeParam",
							listA.stream().map(cn -> new LeftLeafSimilarity<>(ClassNameSizer.CLASS_NAME_SIZER.size(cn), cn)).collect(Collectors.toList())); // TODO wrong: must compare "<A,B...>" with "<>" --> needs a class to represent TypeParameters!				} else {
					} else {
						return ClassNameSimilarityEvaluator.INSTANCE.compareWithGaps(listA, listB);
					}
				}
			}
		}
	}

	public static class ClassDeclarationSimilarity extends CompositeSimilarity {

		protected ClassDeclarationSimilarity(String name, double same, int amount, List<Similarity> qualifiersSim, Similarity nameSimilarity,
			Similarity nestedSubParameters, Similarity extendsClass, List<Similarity> implementedInterfaces, List<Similarity> fieldsSim,
			List<Similarity> innerClassDeclarationSim, List<Similarity> methodsSim, List<Similarity> staticFieldsSim,
			List<Similarity> staticInnerClassDeclarationSim, List<Similarity> staticMethodsSim) {
			super(name, same, amount);
			super.addAll(qualifiersSim, SimilarityChainingHint.start().inLineWithSeparator(" ").endWith(" "));
			super.addOne(nameSimilarity, SimilarityChainingHint.start().inLineWithSeparator(" ").end());
			super.addOne(nestedSubParameters, SimilarityChainingHint.startWith("<").inLineWithSeparator(", ").endWith(">"));
			super.addOne(extendsClass, SimilarityChainingHint.startWith(" extends ").inLine().end());
			super.addAll(implementedInterfaces, SimilarityChainingHint.startWith(" implements ").inLineWithSeparator(", ").end());
			super.addAll(fieldsSim, SimilarityChainingHint.start().newLines().end());
			super.addAll(innerClassDeclarationSim, SimilarityChainingHint.start().newLines().end());
			super.addAll(methodsSim, SimilarityChainingHint.start().newLines().end());
			super.addAll(staticFieldsSim, SimilarityChainingHint.start().newLines().end());
			super.addAll(staticInnerClassDeclarationSim, SimilarityChainingHint.start().newLines().end());
			super.addAll(staticMethodsSim, SimilarityChainingHint.start().newLines().end());
			if (super.contents.isEmpty()) {
				throw new UnsupportedOperationException("No content inside an aggregate???");
			}
		}

		public static ClassDeclarationSimilarity build(List<Similarity> qualifiersSim, Similarity nameSimilarity, Similarity nestedSubParameters,
			Similarity extendsClass, List<Similarity> implementedInterfaces, List<Similarity> fieldsSim, List<Similarity> innerClassDeclarationSim,
			List<Similarity> methodsSim, List<Similarity> staticFieldsSim, List<Similarity> staticInnerClassDeclarationSim, List<Similarity> staticMethodsSim) {
			List<Similarity> allSims = new ArrayList<>();
			allSims.addAll(qualifiersSim);
			allSims.add(nameSimilarity);
			allSims.add(nestedSubParameters);
			allSims.add(extendsClass);
			allSims.addAll(implementedInterfaces);
			allSims.addAll(fieldsSim);
			allSims.addAll(innerClassDeclarationSim);
			allSims.addAll(methodsSim);
			allSims.addAll(staticFieldsSim);
			allSims.addAll(staticInnerClassDeclarationSim);
			allSims.addAll(staticMethodsSim);

			double same = 0;
			int amount = 0;
			for (Similarity similarity : allSims) {
				if (similarity.isEmpty()) {
					continue; // ignore
				}
				same += similarity.getSame();
				amount += similarity.getAmount();
			}
			return new ClassDeclarationSimilarity("class", same, amount, qualifiersSim, nameSimilarity, nestedSubParameters, extendsClass,
				implementedInterfaces, fieldsSim, innerClassDeclarationSim, methodsSim, staticFieldsSim, staticInnerClassDeclarationSim, staticMethodsSim);
		}

		@Override
		public <T> T accept(SimilarityVisitor<T> visitor) {
			return visitor.visit(this);
		}
	}
}
