package diff.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import diff.Levenshtein;
import diff.complexity.Showable;
import diff.complexity.SyntaxSizer;
import diff.similarity.CompositeSimilarity.CompositeSimilarityImpl;
import diff.similarity.SimpleSimilarity.ShowableString;
import diff.similarity.evaluator.ClassDeclarationSimilarityEvaluator.ClassDeclarationSimilarity;
import diff.similarity.evaluator.ClassNameSimilarityEvaluator.ClassNameSimilarity;
import diff.similarity.evaluator.MethodDeclarationSimilarityEvaluator.MethodDeclarationSimilarity;
import diff.similarity.evaluator.RootSimilarityEvaluator.RootSimilarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import diff.similarity.evaluator.expression.VariableDeclarationSimilarityEvaluator.VariableDeclarationSimilarity;
import diff.similarity.evaluator.expression.statement.ChainedAccessSimilarityEvaluator.ChainedAccessSimilarity;
import diff.similarity.evaluator.expression.statement.MethodInvocationSimilarityEvaluator.MethodInvocationSimilarity;
import lexeme.java.tree.expression.statement.primitivetypes.StringValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the similarity analysis between two code trees.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Similarity {

	private final double same;
	private final int amount;

	public abstract Showable showLeft();

	public abstract Showable showRight();

	public abstract List<Similarity> subSimilarities();

	/**
	 * Return the similarity rate.
	 * 
	 * @return the similarity rate (1.0 is exact similarity)
	 */
	public double similarity() {
		return same / amount;
	}

	/**
	 * Holds no information (irrelevant and should be ignored).
	 * 
	 * @return a boolean
	 */
	public boolean isEmpty() {
		return false;
	}

	@Override
	public String toString() {
		final int padding = 60;
		StringBuilder builder = new StringBuilder();
		final List<String[]> show = show("");
		for (String[] strings : show) {
			String formatted = String.format("%0$-" + padding + "." + padding + "s (%0$-5.5s)  %s", strings[0], strings[1], strings[2]);
			builder.append(formatted + "\n");
		}
		return builder.toString();
	}

	/**
	 * Bundles several {@link Similarity} analysis together.
	 * 
	 * @param name
	 *            the name of the aggregate
	 * @param sims
	 *            the smaller similarities
	 * @return a composite similarity
	 */
	public static Similarity add(String name, List<Similarity> sims) {
		return add(name, sims.toArray(new Similarity[sims.size()]));
	}

	/**
	 * Bundles several {@link Similarity} analysis together.
	 * 
	 * @param name
	 *            the name of the aggregate
	 * @param sims
	 *            the smaller similarities
	 * @return a composite similarity
	 */
	public static Similarity add(String name, Similarity... sims) {
		double same = 0;
		int amount = 0;
		List<Similarity> actual = new ArrayList<>();
		for (Similarity similarity : sims) {
			if (similarity.isEmpty()) {
				continue; // ignore
			}
			same += similarity.same;
			amount += similarity.amount;
			actual.add(similarity);
		}
		if (actual.size() == 0) {
			return new NoSimilarity();
		} else if (actual.size() == 1) {
			return actual.get(0);
		} else {
			return new CompositeSimilarityImpl(name, same, amount, actual.toArray(new Similarity[actual.size()]));
		}
	}

	/**
	 * Show the similarity between the both code snippet side by side.
	 * 
	 * @param prefix
	 *            the prefix on the left
	 * @return a {@link List} of lines, each made of two Strings in an Array
	 */
	public abstract List<String[]> show(String prefix);

	/**
	 * Compares two string using Levenshtein distance.
	 * 
	 * @param strA
	 *            string 1
	 * @param strB
	 *            string 2
	 * @return distance : 0 (completely different) ... 1 (all characters
	 *         matching))
	 */
	public static StringSimilarity eval(ShowableString strA, ShowableString strB) {
		int dist = new Levenshtein(strA.getContent(), strB.getContent()).computeDistance();
		int maxLen = Math.max(strA.getContent().length(), strB.getContent().length());
		return new StringSimilarity((maxLen - dist) / (double) maxLen, 1, strA, strB);
	}

	/**
	 * Compares two litteral Strings using Levenshtein distance.
	 * 
	 * @param strA
	 *            string 1
	 * @param strB
	 *            string 2
	 * @return distance : 0 (completely different) ... 1 (all characters
	 *         matching))
	 */
	public static StringSimilarity eval(StringValue strA, StringValue strB) {
		int dist = new Levenshtein(strA.getStringContent(), strB.getStringContent()).computeDistance();
		int maxLen = Math.max(strA.getStringContent().length(), strB.getStringContent().length());
		return new StringSimilarity((maxLen - dist) / (double) maxLen, 1, new ShowableString(strA.getStringContent(), strA.getLocation()),
			new ShowableString(strB.getStringContent(), strB.getLocation()));
	}

	/**
	 * Evaluates the similarity between some optional objects using an evaluator
	 * and a sizer
	 * 
	 * @param <T>
	 *            the type of objects evaluated
	 * @param optA
	 *            the left object (optional)
	 * @param optB
	 *            the right object (optional)
	 * @param simEval
	 *            a similarity evaluator for an object of type T
	 * @param sizer
	 *            a sizer for an object of type T
	 * @return the similarity analysis
	 */
	public static <T extends Showable> Similarity eval(Optional<? extends T> optA, Optional<? extends T> optB, SimilarityEvaluator<T> simEval,
		SyntaxSizer<T> sizer) {
		if (optA.isPresent()) {
			if (optB.isPresent()) {
				return simEval.eval(optA.get(), optB.get());
			} else {
				return new LeftLeafSimilarity<T>(sizer.size(optA.get()), optA.get());
			}
		} else {
			if (optB.isPresent()) {
				return new RightLeafSimilarity<T>(sizer.size(optB.get()), optB.get());
			} else {
				return new NoSimilarity();
			}
		}
	}

	public static <T extends Showable> Similarity bestOf(Similarity... sims) {
		double bestValue = -1;
		Similarity bestSim = new NoSimilarity();
		for (Similarity similarity : sims) {
			double val = similarity.similarity();
			if (val > bestValue) {
				bestValue = val;
				bestSim = similarity;
			}
		}
		return bestSim;
	}

	public abstract <T> T accept(SimilarityVisitor<T> visitor);

	public static interface SimilarityVisitor<T> {

		T visit(CompositeSimilarityImpl similarity);

		T visit(ChainedAccessSimilarity chainedAccessSimilarity);

		T visit(ClassNameSimilarity classNameSimilarity);

		T visit(ClassDeclarationSimilarity classSimilarity);

		T visit(VariableDeclarationSimilarity variableDeclarationSimilarity);

		T visit(MethodInvocationSimilarity methodInvocationSimilarity);

		<S extends Showable> T visit(LeafSimilarity<S> similarity);

		<S extends Showable> T visit(LeftLeafSimilarity<S> similarity);

		<S extends Showable> T visit(RightLeafSimilarity<S> similarity);

		T visit(SimpleSimilarity similarity);

		T visit(NoSimilarity similarity);

		T visit(RootSimilarity rootSimilarity);

		T visit(MethodDeclarationSimilarity methodDeclarationSimilarity);

	}
}
