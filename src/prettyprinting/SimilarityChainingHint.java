package prettyprinting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimilarityChainingHint {

	final String prefix; // append to previous if startInNewLine then offset body. Otherwise separator to previous content
	final boolean newLines;
	final String separator; // prefix if newLineEverySimilarity, separator if not
	final String suffix;

	@Override
	public String toString() {
		return '"' + prefix + "\"(" + (newLines ? "\\n" : "") + separator + "\")\"" + suffix + '"';
	}

	public static RepeatDefinition startWith(String prefix) {
		return new RepeatDefinition(prefix);
	}

	public static RepeatDefinition start() {
		return new RepeatDefinition("");
	}

	@Getter
	@AllArgsConstructor
	public static class RepeatDefinition {
		final String prefix;

		public OneTimeCloserDefinition newLinesWithPrefix(String lineStart) {
			return new OneTimeCloserDefinition(prefix, true, lineStart);
		}

		public OneTimeCloserDefinition newLines() {
			return new OneTimeCloserDefinition(prefix, true, "");
		}

		public OneTimeCloserDefinition inLineWithSeparator(String separator) {
			return new OneTimeCloserDefinition(prefix, false, separator);
		}

		public OneTimeCloserDefinition inLine() {
			return new OneTimeCloserDefinition(prefix, false, "");
		}
	}

	@Getter
	@AllArgsConstructor
	public static class OneTimeCloserDefinition {
		final String prefix;
		final boolean newLines;
		final String separator;

		public SimilarityChainingHint endWith(String suffix) {
			return new SimilarityChainingHint(prefix, newLines, separator, suffix);
		}

		public SimilarityChainingHint end() {
			return new SimilarityChainingHint(prefix, newLines, separator, "");
		}
	}
}
