package lexer;

import java.util.Optional;

import tokenizer.TokenStream;

/**
 * Constructs a {@link Structure} from tokens.
 * @param <G> a {@link Grammar}
 * @param <S> a potential constructed {@link Structure}
 */
@FunctionalInterface
public interface StructureLexer<G extends Grammar, S extends Structure<G>> {

    /**
     * Reads a {@link TokenStream} and attempts to build a {@link Structure} out of it.<br>
     * If it succeeds, returns the structure, and commits (advances) the change to the stream.<br>
     * If it fails, returns an empty {@link Optional} and leaves the input stream unchanged
     * @param input the input stream
     * @return Optionally, a {@link Structure}
     */
    Optional<? extends S> lex(TokenStream<G> input);

}
