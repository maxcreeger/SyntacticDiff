package tokenizer.tokens;

import lexer.Grammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeLocation;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.TokenVisitor;
import tokenizer.usual.GenericTokenizer;
import tokenizer.usual.GenericTokenizer.TokenBuilder;

/**
 * A line feed or carriage return.
 *
 * @param <G> a grammar
 */
@Getter
@AllArgsConstructor
public class LineFeed<G extends Grammar> implements Token<G> {

    final CodeLocation location;


    /**
     * Detects line feeds.
     *
     * @param <G> a grammar
     */
    public static class LineFeedTokenizer<G extends Grammar> extends GenericTokenizer<G, LineFeed<G>> {

        protected LineFeedTokenizer() {
            super("(\\n+)", new LineFeedBuilder<>());
        }

    }

    /**
     * Creates a classic line feed tokenizer.
     * 
     * @param <G> a grammar
     * @return a tokenizer
     */
    public static <G extends Grammar> LineFeedTokenizer<G> tokenizer() {
        return new LineFeedTokenizer<>();
    }

    @Override
    public String toString() {
        return "(EOL)";
    }

    @Override
    public String get() {
        return "\n";
    }

    @Override
    public TokenType getTokenType() {
        return TokenType.LINE_FEED;
    }

    private static class LineFeedBuilder<G extends Grammar> implements TokenBuilder<G, LineFeed<G>> {
        @Override
        public LineFeed<G> build(CodeLocation code) {
            return new LineFeed<G>(code);
        }
    }

    @Override
    public <T> T accept(TokenVisitor<G, T> visitor) {
        return visitor.visit(this);
    }

}
