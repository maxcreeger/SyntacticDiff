package diff.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import diff.complexity.Showable;
import lombok.Getter;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Comparison analysis between nothing on the left, and a right side code.
 *
 * @param <S>
 *            the type of object analyzed on the right
 */
@Getter
public class RightLeafSimilarity<S extends Showable> extends Similarity {

	public static final class NoShowable implements Showable {
		@Override
		public List<String> fullBreakdown(String prefix) {
			return new ArrayList<>();
		}

		@Override
		public String toString() {
			return "<NoShowable>";
		}

		@Override
		public CodeLocation getLocation() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	protected final S obj2;

	/**
	 * Build a similarity analysis between nothing on the left and an optional
	 * right Object.
	 * 
	 * @param <S>
	 *            the type of object analyzed
	 * @param amount
	 *            the size
	 * @param optionalObj2
	 *            the optional right object
	 * @return a {@link Similarity}
	 */
	public static <S extends Showable> Similarity build(int amount, Optional<S> optionalObj2) {
		if (optionalObj2.isPresent()) {
			return new RightLeafSimilarity<>(amount, optionalObj2.get());
		} else {
			return new NoSimilarity();
		}
	}

	/**
	 * Build a similarity analysis between nothing on the left and a an Object
	 * on the right.<br>
	 * 
	 * @param <S>
	 *            the type of object analyzed
	 * @param amount
	 *            the size
	 * @param obj2
	 *            the left object
	 * @return a {@link Similarity}
	 */
	public static <S extends Showable> Similarity build(int amount, S obj2) {
		return new RightLeafSimilarity<>(amount, obj2);
	}

	/**
	 * Build a similarity analysis between nothing on the left and a right
	 * Object.
	 * 
	 * @param amount
	 *            the size
	 * @param obj2
	 *            the right object
	 */
	public RightLeafSimilarity(int amount, S obj2) {
		super(0, amount);
		this.obj2 = obj2;
	}

	@Override
	public List<String[]> show(String prefix) {
		List<String[]> result = new ArrayList<>();
		for (String line : obj2.fullBreakdown(prefix)) {
			result.add(new String[] { prefix + "<Nothing>", "RIGHT", line });
		}
		return result;
	}

	@Override
	public String toString() {
		return "<Right only=" + obj2 + ">";
	}

	@Override
	public <R> R accept(SimilarityVisitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public Showable showLeft() {
		return new NoShowable();
	}

	@Override
	public Showable showRight() {
		return obj2;
	}

	@Override
	public List<Similarity> subSimilarities() {
		// TODO Auto-generated method stub
		return null;
	}
}
