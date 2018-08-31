package tokenizer;

import java.util.Optional;

import lexer.Grammar;
import tokenizer.tokens.LineFeed;
import tokenizer.tokens.Number;
import tokenizer.tokens.Symbol;
import tokenizer.tokens.Whitespace;
import tokenizer.tokens.Word;

/**
 * Tyical visitor pattern for {@link Token}s.
 *
 * @param <G> a grammar
 * @param <T> a targeted token type
 */
public interface TokenVisitor<G extends Grammar, T> {

    /**
     * Visits a token which may not be there.
     * @param token the optional token
     * @return the result ({@link #defaultResult()} if the token is absent or null)
     */
    default T visitAny(Optional<Token<G>> token) {
        return token.isPresent() ? visitAny(token.get()) : defaultResult();
    }

    /**
     * Visits a token of unknown type, which may be null.
     * @param token the nullable token
     * @return the result ({@link #defaultResult()} if the token is null)
     */
    default T visitAny(Token<G> token) {
        return token == null ? defaultResult() : token.accept(this);
    }

    /**
     * When the input is null or invalid and its type is unknown.
     * @return a default result
     */
    T defaultResult();

    /**
     * Visit a {@link Word}
     * @param token the word
     * @return the result
     */
    T visit(Word<G> token);


    /**
     * Visit a {@link Number}
     * @param token the number
     * @return the result
     */
    T visit(Number<G> token);

    /**
     * Visit a {@link Symbol}
     * @param token the Symbol
     * @return the result
     */
    T visit(Symbol<G> token);

    /**
     * Visit a {@link LineFeed}
     * @param token the LineFeed
     * @return the result
     */
    T visit(LineFeed<G> token);

    /**
     * Visit a {@link Whitespace}
     * @param token the Whitespace
     * @return the result
     */
    T visit(Whitespace<G> token);
}
