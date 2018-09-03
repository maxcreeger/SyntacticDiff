package tokenizer.tokens;

import lexer.Grammar;
import lexer.usual.structure.SingleTokenStructure;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.TokenVisitor;
import tokenizer.Tokenizer;
import tokenizer.usual.GenericTokenizer;
import tokenizer.usual.GenericTokenizer.TokenBuilder;

/**
 * Represents a word (that is not a known, system-reserved {@link Symbol} of the
 * {@link Grammar}.
 *
 * @param <G>
 *            a {@link Grammar}
 */
public interface Word<G extends Grammar> extends Token<G>, SingleTokenStructure<G> {


	/**
	 * {@link Tokenizer} which produces {@link Word}s.
	 *
	 * @param <G>
	 *            a {@link Grammar}
	 */
	public static class WordTokenizer<G extends Grammar> extends GenericTokenizer<G, Word<G>> implements Tokenizer<G, Word<G>> {

		protected WordTokenizer() {
			super("(\\w+)", new WordBuilder<>());
		}

	}

    @Getter
    @AllArgsConstructor
    public static class WordImpl<G extends Grammar> implements Word<G> {

        private final String word;

        @Override
        public String toString() {
            return "(Word: \"" + word + "\")";
        }
    }

	static class WordBuilder<G extends Grammar> implements TokenBuilder<G, Word<G>> {
		@Override
        public Word<G> build(String str) {
            return new WordImpl<G>(str);
		}
	}

	/**
	 * Returns a generic {@link WordTokenizer}. Recognizes any "\W+".
	 * 
	 * @param <G>
	 *            a {@link Grammar}
	 * @return the word tokenizer
	 */
	public static <G extends Grammar> WordTokenizer<G> tokenizer() {
		return new WordTokenizer<G>();
	}

    public default String get() {
		return getWord();
	}

    public String getWord();

    public default TokenType getTokenType() {
		return TokenType.WORD;
	}

	@Override
    public default <T> T accept(TokenVisitor<G, T> visitor) {
		return visitor.visit(this);
	}

	@Override
    public default Word<G> getToken() {
		return this;
	}

}
