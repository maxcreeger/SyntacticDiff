package tokenizer;

import lexer.Grammar;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents an atomic token (symbol, keyword, operator...) found in the code, which holds a meaning.
 *
 * @param <G> a {@link Grammar} explaining the role of the token
 */
public interface Token<G extends Grammar> {

    /**
     * Returns the String representation of the token in the original code.
     * 
     * @return a String$
     */
    default String get() {
        return getLocation().getCode();
    }

    /**
     * The {@link TokenType}
     * 
     * @return the {@link TokenType}
     */
    TokenType getTokenType();

    /**
     * Visitor pattern.
     * 
     * @param <T> the result type
     * @param visitor a {@link TokenVisitor}
     * @return the result
     */
    <T> T accept(TokenVisitor<G, T> visitor);

    /**
     * Tells if this token is actually representing the End Of File (EOF).
     * 
     * @return a {@link Boolean}
     */
    default boolean endOfStream() {
        return false;
    }

    CodeLocation getLocation();

}
