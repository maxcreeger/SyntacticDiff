package diff.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import diff.complexity.Showable;
import diff.similarity.RightLeafSimilarity.NoShowable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import prettyprinting.SimilarityChainingHint;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Aggregates several {@link Similarity} analysis in a single one.
 */
@Getter
public abstract class CompositeSimilarity extends Similarity {

	@Getter
	@AllArgsConstructor
	public static class HintedShowable {
		SimilarityChainingHint hint;
		Showable showable;

		List<String> fullBreakdown(List<String> previous) {
			List<String> fullBreakdown = showable.fullBreakdown("");
			if (previous == null || previous.isEmpty()) {
				return fullBreakdown;
			}
			List<String> result = new ArrayList<>();
			if (hint.isNewLines()) {
				// above
				for (int i = 0; i < previous.size() - 1; i++) {
					result.add(previous.get(i));
				}
				// prefix
				result.add(previous.get(previous.size() - 1) + hint.getPrefix());
				// Content
				for (int i = 0; i < fullBreakdown.size(); i++) {
					result.add(hint.getSeparator() + fullBreakdown.get(i));
				}
				// suffix
				result.add(hint.getSuffix());
				return result;
			} else {
				// Above
				for (int i = 0; i < previous.size() - 1; i++) {
					result.add(previous.get(i));
				}
				String firstLine = previous.isEmpty() ? "" : previous.get(previous.size() - 1);
				// prefix
				firstLine = firstLine + hint.getPrefix();
				// Content
				for (int i = 0; i < fullBreakdown.size(); i++) {
					firstLine += hint.getSeparator() + fullBreakdown.get(i);
				}
				// suffix
				firstLine += hint.getSuffix();
				result.add(firstLine);
				return result;
			}
		}

		public List<String> nativeFormat(List<String> previous) {
			List<String> fullBreakdown = showable.nativeFormat("");
			if (previous == null || previous.isEmpty()) {
				return fullBreakdown;
			}
			List<String> result = new ArrayList<>();
			if (hint.isNewLines()) {
				// above
				for (int i = 0; i < previous.size() - 1; i++) {
					result.add(previous.get(i));
				}
				// prefix
				result.add(previous.get(previous.size() - 1) + hint.getPrefix());
				// Content
				for (int i = 0; i < fullBreakdown.size(); i++) {
					result.add(hint.getSeparator() + fullBreakdown.get(i));
				}
				// suffix
				result.add(hint.getSuffix());
				return result;
			} else {
				// Above
				for (int i = 0; i < previous.size() - 1; i++) {
					result.add(previous.get(i));
				}
				String firstLine = previous.isEmpty() ? "" : previous.get(previous.size() - 1);
				// prefix
				firstLine = firstLine + hint.getPrefix();
				// Content
				for (int i = 0; i < fullBreakdown.size(); i++) {
					firstLine += hint.getSeparator() + fullBreakdown.get(i);
				}
				// suffix
				firstLine += hint.getSuffix();
				result.add(firstLine);
				return result;
			}
		}

		@Override
		public String toString() {
			return showable.toString();
		}
	}

	public static class CompositeSimilarityImpl extends CompositeSimilarity {

		public CompositeSimilarityImpl(String name, double same, int amount, Similarity[] contents) {
			super(name, same, amount);
			for (Similarity content : contents) {
				super.addOne(content); // TODO this is by default. Must be recplaced by ad-hoc code
			}
			if (super.contents.isEmpty()) {
				throw new UnsupportedOperationException("No content inside an aggregate???");
			}
		}

		@Override
		public <T> T accept(SimilarityVisitor<T> visitor) {
			return visitor.visit(this);
		}

	}

	@Getter
	public static final class AggregateShowable implements Showable {

		private final List<HintedShowable> allContent = new ArrayList<>();
		private CodeLocation location;

		public AggregateShowable(List<HintedShowable> showables) {
			for (HintedShowable hintedShowable : showables) {
				if (hintedShowable == null || hintedShowable.getShowable() instanceof NoShowable) {
					return;
				}
				if (location == null) {
					location = hintedShowable.getShowable().getLocation();
				} else {
					location = location.merge(hintedShowable.getShowable().getLocation());
				}
			}
			allContent.addAll(showables);

			if (allContent.isEmpty()) {
				throw new UnsupportedOperationException("No content inside an aggregate???");
			}
		}

		public void add(Showable showable, SimilarityChainingHint hint) {
			if (allContent.isEmpty()) {
				throw new UnsupportedOperationException("No content inside an aggregate???");
			}
			if (showable == null || showable instanceof NoShowable) {
				return;
			}
			allContent.add(new HintedShowable(hint, showable));
			if (location == null) {
				location = showable.getLocation();
			} else {
				location = location.merge(showable.getLocation());
			}
		}

		@Override
		public List<String> fullBreakdown(String prefix) {

			if (allContent.isEmpty()) {
				throw new UnsupportedOperationException("No content inside an aggregate???");
			}
			List<String> prev = null;
			for (HintedShowable content : allContent) {
				prev = content.fullBreakdown(prev);
			}
			return prev.stream().map(line -> prefix + line).collect(Collectors.toList());
		}

		@Override
		public List<String> nativeFormat(String prefix) {

			if (allContent.isEmpty()) {
				throw new UnsupportedOperationException("No content inside an aggregate???");
			}
			List<String> prev = null;
			for (HintedShowable content : allContent) {
				prev = content.nativeFormat(prev);
			}
			return prev.stream().map(line -> prefix + line).collect(Collectors.toList());
		}

		@Override
		public String toString() {
			if (allContent.isEmpty()) {
				throw new UnsupportedOperationException("No content inside an aggregate???");
			}
			return String.join("\n", nativeFormat(""));
		}
	}

	@Getter
	@AllArgsConstructor
	public static class HintedSimilarity {
		final SimilarityChainingHint hint;
		final Similarity similarity;

		HintedShowable showLeft() {
			return new HintedShowable(hint, similarity.showLeft());
		}

		HintedShowable showRight() {
			return new HintedShowable(hint, similarity.showRight());
		}

		@Override
		public String toString() {
			return similarity.toString();
		}
	}

	private static final SimilarityChainingHint DEFAULT_HINT = SimilarityChainingHint.startWith(" ").newLinesWithPrefix("  ").endWith("");

	protected final String name;
	protected final List<HintedSimilarity> contents = new ArrayList<>();

	/**
	 * Builds a Similarity analysis out of several smaller ones. under a common
	 * function.
	 * 
	 * @param name
	 *            name of the aggregate
	 * @param same
	 *            the similarity
	 * @param amount
	 *            the size
	 */
	protected CompositeSimilarity(String name, double same, int amount) {
		super(same, amount);
		this.name = name;
	}

	protected void addOne(Similarity sim) {
		contents.add(new HintedSimilarity(DEFAULT_HINT, sim));
	}

	protected void addAll(List<Similarity> similarities) {
		for (Similarity sim : similarities) {
			contents.add(new HintedSimilarity(DEFAULT_HINT, sim));
		}
	}

	protected void addOne(Similarity sim, SimilarityChainingHint hint) {
		contents.add(new HintedSimilarity(hint, sim));
	}

	protected void addAll(List<Similarity> similarities, SimilarityChainingHint hint) {
		for (Similarity sim : similarities) {
			contents.add(new HintedSimilarity(hint, sim));
		}
	}

	@Override
	public List<String[]> show(String prefix) {
		List<String[]> result = new ArrayList<>();
		String header = "(" + name + ")";
		final String[] headerLine = new String[] { header, Double.toString(similarity()), header };
		result.add(headerLine);
		for (int s = 0; s < contents.size(); s++) {
			Similarity similarity = contents.get(s).getSimilarity();
			String separator = s < contents.size() - 1 ? "|  " : "   ";
			List<String[]> show = similarity.show("");
			for (int i = 0; i < show.size(); i++) {
				String pref;
				if (i == 0) {
					pref = "+��";
				} else {
					pref = separator;
				}
				String[] strings = show.get(i);
				result.add(new String[] { pref + strings[0], strings[1], pref + strings[2] });
			}
		}
		return result;
	}

	@Override
	public Showable showLeft() {
		List<HintedShowable> allLeft = new ArrayList<>();
		for (HintedSimilarity hintedSim : contents) {
			if (!(hintedSim.getSimilarity() instanceof NoSimilarity)) {
				HintedShowable left = hintedSim.showLeft();
				allLeft.add(left);
			}
		}
		AggregateShowable aggr = new AggregateShowable(allLeft);
		return aggr;
	}

	@Override
	public Showable showRight() {
		List<HintedShowable> allRight = new ArrayList<>();
		for (HintedSimilarity hintedSim : contents) {
			if (!(hintedSim.getSimilarity() instanceof NoSimilarity)) {
				HintedShowable right = hintedSim.showLeft();
				allRight.add(right);
			}
		}
		AggregateShowable aggr = new AggregateShowable(allRight);
		return aggr;
	}

	@Override
	public List<Similarity> subSimilarities() {
		return contents.stream().map(HintedSimilarity::getSimilarity).collect(Collectors.toList());
	}
}
