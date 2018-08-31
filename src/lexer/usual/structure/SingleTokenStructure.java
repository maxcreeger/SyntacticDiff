package lexer.usual.structure;

import lexer.Grammar;
import lexer.Structure;
import tokenizer.Token;

/**
 * Lexer that finds a specific kind of token.
 *
 * @param <G> a {@link Grammar}
 */
public interface SingleTokenStructure<G extends Grammar> extends Structure<G> {

    /**
     * Get the {@link Token}.
     * @return the token
     */
    public Token<G> getToken();

}
