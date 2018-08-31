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
import tokenizer.tokens.Word;

/**
 * Builds a word.
 *
 * @param <G> a {@link Grammar}
 */
@Getter
@AllArgsConstructor
public class SingleWordStructureLexer<G extends Grammar> implements StructureLexer<G, Word<G>> {

    private final Predicate<Word<G>> wordDetector;

    @Override
    public Optional<Word<G>> lex(TokenStream<G> input) {
        if (!input.hasNext()) {
            return Optional.empty();
        }
        TokenStream<G> fork = input.fork();
        Token<G> found = fork.next();
        if (found.getTokenType() == TokenType.WORD && wordDetector.test((Word<G>) found)) {
            fork.commit();
            return Optional.of(build((Word<G>) found));
        } else {
            return Optional.empty();
        }
    }

    protected Word<G> build(Word<G> token) {
        return token;
    }

}
