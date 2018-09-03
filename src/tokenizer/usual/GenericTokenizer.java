package tokenizer.usual;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexer.Grammar;
import tokenizer.Token;
import tokenizer.Tokenizer;

/**
 * Helper class to create {@link Tokenizer} using a {@link Pattern}.
 *
 * @param <G>
 *            a {@link Grammar}
 * @param <T>
 *            a {@link TokenBuilder} type
 */
public class GenericTokenizer<G extends Grammar, T extends Token<G>> implements Tokenizer<G, T> {

	private final Pattern pattern;
	private final TokenBuilder<G, ? extends T> builder;

	/**
	 * Creates a {@link GenericTokenizer} using a pattern, and a token builder.
	 * 
	 * @param patternString
	 *            a pattern String
	 * @param builder
	 *            a {@link TokenBuilder}
	 */
	public GenericTokenizer(String patternString, TokenBuilder<G, ? extends T> builder) {
		pattern = Pattern.compile(patternString);
		this.builder = builder;
	}

	@Override
	public Optional<T> tokenize(AtomicReference<String> input) {
		Matcher matcher = pattern.matcher(input.get());
		if (matcher.lookingAt()) {
			// Commit
            String match = input.get().substring(matcher.start(1), matcher.end(1));
            String remainder = input.get().substring(matcher.end(1));
			input.set(remainder);
            return Optional.of(builder.build(match));
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Interface for {@link Token} builders.
	 *
	 * @param <G>
	 *            a {@link Grammar}
	 * @param <T>
	 *            the type of {@link Token} that can be built
	 */
	public static interface TokenBuilder<G extends Grammar, T extends Token<G>> {

		/**
		 * Build a {@link Token} using the provided String as code
		 * 
		 * @param str
		 *            the code to analyze
		 * @return the built token
		 */
        T build(String str);
	}

}
