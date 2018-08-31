package tokenizer.usual;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import lexer.Grammar;
import tokenizer.Token;
import tokenizer.Tokenizer;

/**
 * Creates a {@link Tokenizer} that federates many other tokenizers. to tokenize an input string.
 *
 * @param <G> a common {@link Grammar}
 * @param <T> a common sub-type of {@link Token}
 */
public class AggregateTokenizer<G extends Grammar, T extends Token<G>> implements Tokenizer<G, T> {

    List<Tokenizer<G, ? extends T>> tokenizers = new ArrayList<>();

    /**
     * Constructs a tokenizer that leverages the provided tokenizers to tokenize an input string.
     * @param tokenizers the tokenizers
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

    /**
     * Tokenize all the input string into tokens.
     * @param input the input
     * @return a {@link List} of {@link Token}. If any character sequence in input String fails to be tokenized, returns an empty {@link Optional}
     */
    public Optional<List<T>> tokenizeAll(AtomicReference<String> input) {
        AtomicReference<String> defensiveCopy = new AtomicReference<String>(input.get());
        List<T> result = new ArrayList<>();
        parse: while (!defensiveCopy.get().isEmpty()) {
            for (Tokenizer<G, ? extends T> tokenizer : tokenizers) {
                Optional<? extends T> token = tokenizer.tokenize(defensiveCopy);
                if (token.isPresent()) {
                    result.add(token.get());
                    continue parse;
                }
            }
            // Unable to decipher the token
            return Optional.empty();
        }
        // Parsed all the input, commit & return all that was found
        input.set(defensiveCopy.get());
        return Optional.of(result);
    }

}
