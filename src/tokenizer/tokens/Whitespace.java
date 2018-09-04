package tokenizer.tokens;

import lexer.Grammar;
import lexer.usual.structure.SingleTokenStructure;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeLocation;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.TokenVisitor;
import tokenizer.Tokenizer;
import tokenizer.usual.GenericTokenizer;
import tokenizer.usual.GenericTokenizer.TokenBuilder;

/**
 * A Token representing a whitespace.
 *
 * @param <G> a {@link Grammar}
 */
@Getter
@AllArgsConstructor
public class Whitespace<G extends Grammar> implements Token<G>, SingleTokenStructure<G> {

    final CodeLocation location;

    /**
     * {@link Tokenizer} which builds {@link Whitespace}s (consecutive ' 's or '\t's) from an input string.
     * @param <G> a {@link Grammar}
     */
    public static class WhitespaceTokenizer<G extends Grammar> extends GenericTokenizer<G, Whitespace<G>> {

        protected WhitespaceTokenizer() {
            super("([ \t]+)", new WhitespaceBuilder<>());
        }
    }

    /**
     * Returns a standard whitespace detector.
     * @param <G> a {@link Grammar}
     * @return a
     */
    public static <G extends Grammar> WhitespaceTokenizer<G> tokenizer() {
        return new WhitespaceTokenizer<>();
    }

    @Override
    public String toString() {
        return "(whitespace) ";
    }

    @Override
    public String get() {
        return " ";
    }

    @Override
    public TokenType getTokenType() {
        return TokenType.WHITESPACE;
    }

    private static class WhitespaceBuilder<G extends Grammar> implements TokenBuilder<G, Whitespace<G>> {
        @Override
        public Whitespace<G> build(CodeLocation code) {
            return new Whitespace<G>(code);
        }
    }

    @Override
    public <T> T accept(TokenVisitor<G, T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Token<G> getToken() {
        return this;
    }

}
