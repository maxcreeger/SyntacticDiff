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
import tokenizer.tokens.Symbol;

/**
 * Builds a {@link Symbol} (structure) from a {@link Symbol} (token) using a detector.
 *
 * @param <G> a {@link Grammar}
 */
@AllArgsConstructor
@Getter
public class SingleSymbolStructureLexer<G extends Grammar> implements StructureLexer<G, Symbol<G>> {

    private final Predicate<Symbol<G>> symbolDetector;

    @Override
    public Optional<Symbol<G>> lex(TokenStream<G> input) {
        if (!input.hasNext()) {
            return Optional.empty();
        }
        TokenStream<G> fork = input.fork();
        Token<G> found = fork.next();
        if (found.getTokenType() == TokenType.SYMBOL && symbolDetector.test((Symbol<G>) found)) {
            fork.commit();
            return Optional.of(build((Symbol<G>) found));
        } else {
            return Optional.empty();
        }
    }

    protected Symbol<G> build(Symbol<G> token) {
        return token;
    }

}
