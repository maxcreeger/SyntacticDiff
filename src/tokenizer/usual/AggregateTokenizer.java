package tokenizer.usual;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import lexer.Grammar;
import tokenizer.Token;
import tokenizer.Tokenizer;

/**
 * Creates a {@link Tokenizer} that federates many other tokenizers to tokenize
 * an input string.
 *
 * @param <G>
 *            a common {@link Grammar}
 * @param <T>
 *            a common sub-type of {@link Token}
 */
public class AggregateTokenizer<G extends Grammar, T extends Token<G>> implements Tokenizer<G, T> {

	List<Tokenizer<G, ? extends T>> tokenizers = new ArrayList<>();

	/**
	 * Constructs a tokenizer that leverages the provided tokenizers to tokenize
	 * an input string.
	 * 
	 * @param tokenizers
	 *            the tokenizers
	 */
	public AggregateTokenizer(List<Tokenizer<G, ? extends T>> tokenizers) {
		this.tokenizers = new ArrayList<>(tokenizers);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<T> tokenize(AtomicReference<String> input) {
		for (Tokenizer<G, ? extends T> tokenizer : tokenizers) {
			Optional<? extends T> token = tokenizer.tokenize(input);
			if (token.isPresent()) {
				return (Optional<T>) token;
			}
		}
		return Optional.empty();
	}

}
