package lexer.usual.structure;

import java.util.Optional;
import java.util.function.Predicate;

import lexer.Grammar;
import lexer.StructureLexer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.Token;
import tokenizer.TokenStream;
import tokenizer.TokenType;
import tokenizer.tokens.Whitespace;


/**
 * Builds a {@link Whitespace} structure
 *
 * @param <G> a {@link Grammar}
 */
@AllArgsConstructor
@Getter
public class SingleWhitespaceStructureLexer<G extends Grammar> implements StructureLexer<G, Whitespace<G>> {

    private final Predicate<Whitespace<G>> whitespaceDetector;

    @Override
    public Optional<Whitespace<G>> lex(TokenStream<G> input) {
        if (!input.hasNext()) {
            return Optional.empty();
        }
        TokenStream<G> fork = input.fork();
        Token<G> found = fork.next();
        if (found.getTokenType() == TokenType.WHITESPACE && whitespaceDetector.test((Whitespace<G>) found)) {
            fork.commit();
            return Optional.of(build((Whitespace<G>) found));
        } else {
            return Optional.empty();
        }
    }

    protected Whitespace<G> build(Whitespace<G> token) {
        return token;
    }

}
