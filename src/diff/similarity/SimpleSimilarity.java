package diff.similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import diff.complexity.Showable;
import lexeme.java.tree.JavaWhitespace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Simple similarity (placeholder to present differences)
 */
@Getter
public class SimpleSimilarity extends Similarity {

	@Getter
	@AllArgsConstructor
	public static class ShowableString implements Showable {

		String content;
		CodeLocation location;

		public static Optional<ShowableString> fromPattern(CodeBranch input, Pattern pattern) {
			CodeBranch fork = input.fork();
			Matcher classNameMatcher = pattern.matcher(fork.getRest());
			if (!classNameMatcher.lookingAt()) {
				// Not a class name!
				return Optional.empty();
			}
			// Advancing input to end of class name
			fork.advance(classNameMatcher.group(0).length());
			JavaWhitespace.skipWhitespaceAndComments(fork);
			return Optional.of(new ShowableString(classNameMatcher.group(0), fork.commit()));
		}

		@Override
		public List<String> fullBreakdown(String prefix) {
			List<String> list = new ArrayList<>();
			list.add(content);
			return list;
		}

		@Override
		public String toString() {
			return String.join("\n", fullBreakdown(""));
		}
	}

	private final ShowableString leftComment;
	private final String diff;
	private final ShowableString rightComment;

	@Override
	public Showable showLeft() {
		return leftComment;
	}

	@Override
	public Showable showRight() {
		return rightComment;
	}

	@Override
	public List<Similarity> subSimilarities() {
		return new ArrayList<>();
	}

	/**
	 * Builds a {@link SimpleSimilarity} using messages for left & right sides
	 * 
	 * @param same
	 *            similarity metric
	 * @param amount
	 *            sizing metric
	 * @param leftComment
	 *            left-side message
	 * @param diff
	 *            difference message (1, 0, ...)
	 * @param rightComment
	 *            right-side message
	 */
	public SimpleSimilarity(double same, int amount, ShowableString leftComment, String diff, ShowableString rightComment) {
		super(same, amount);
		this.leftComment = leftComment;
		this.diff = diff;
		this.rightComment = rightComment;
	}

	@Override
	public List<String[]> show(String prefix) {
		List<String[]> list = new ArrayList<>();
		list.add(new String[] { prefix + leftComment, diff, prefix + rightComment });
		return list;
	}

	@Override
	public <R> R accept(SimilarityVisitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return String.join("\n", show("").stream().flatMap(array -> Arrays.stream(array)).collect(Collectors.toList()));
	}

}
