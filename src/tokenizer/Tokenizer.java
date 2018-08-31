package tokenizer;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

import lexer.Grammar;

/**
 * Entity able to recognize a {@link Token} from a {@link String} input. Could be a class name, an operator...
 * @param <G> a {@link Grammar}
 * @param <T> the type of {@link Token}s that can be recognized.
 */
public interface Tokenizer<G extends Grammar, T extends Token<G>> {

    /**
     * Attempts to find this token in the provided String.
     * @param input the reference String to scan. Is mutated if a match is found
     * @return a {@link Matcher} which may or may not contain a match.
     */
    Optional<T> tokenize(AtomicReference<String> input);

}
