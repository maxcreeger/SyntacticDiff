package tokenizer.tokens;

import lexer.Grammar;
import lexer.usual.structure.SingleTokenStructure;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.TokenVisitor;
import tokenizer.Tokenizer;
import tokenizer.usual.GenericTokenizer;
import tokenizer.usual.GenericTokenizer.TokenBuilder;

/**
 * Represents any system-reserved symbol of the language.
 *
 * @param <G> a {@link Grammar}
 */
// @Getter
// @AllArgsConstructor
public interface Symbol<G extends Grammar> extends Token<G>, SingleTokenStructure<G> {

    // private final String symbol;

    /**
     * Returns the symbol representation .
     * 
     * @return a string
     */
    public String getSymbol();

    /**
     * {@link Tokenizer} for {@link Symbol}s. Uses a pattern.
     * @param <G> a {@link Grammar}
     */
    public static class SymbolTokenizer<G extends Grammar> extends GenericTokenizer<G, Symbol<G>> {

        private final String symbolStandardRepresentation;

        /**
         * Construct {@link SymbolTokenizer}s.
         * @param patternString a pattern, with a single group surrounding the symbol
         * @param symbol the standard representation of the symbol
         */
        protected SymbolTokenizer(String patternString, String symbol) {
            super(patternString, new SymbolBuilder<>());
            this.symbolStandardRepresentation = symbol;
        }

        @Override
        public String toString() {
            return "Tokenizer for [" + symbolStandardRepresentation + "]";
        }

    }

    /**
     * Helper method to construct {@link SymbolTokenizer}s.
     * @param <G> a {@link Grammar}
     * @param patternString a pattern, with a single group surrounding the symbol
     * @param symbol the standard representation of the symbol
     * @return a {@link SymbolTokenizer}
     */
    public static <G extends Grammar> SymbolTokenizer<G> tokenizer(String patternString, String symbol) {
        return new SymbolTokenizer<>(patternString, symbol);
    }

    @Override
    public default String get() {
        return getSymbol();
    }

    @Override
    public default TokenType getTokenType() {
        return TokenType.SYMBOL;
    }

    /**
     * Builds a symbol.
     *
     * @param <G> a grammar
     */
    static class SymbolBuilder<G extends Grammar> implements TokenBuilder<G, Symbol<G>> {
        @Override
        public Symbol<G> build(String str) {
            return new SymbolImpl<G>(str);
        }
    }

    @Override
    public default <T> T accept(TokenVisitor<G, T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public default Token<G> getToken() {
        return this;
    }

}
