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
 * Represents a number.
 *
 * @param <G>
 *            a grammar
 */
@Getter
@AllArgsConstructor
public class Number<G extends Grammar> implements Token<G>, SingleTokenStructure<G> {

	private final int num;

	/**
	 * Tokenizer that reads numbers.
	 *
	 * @param <G>
	 *            a grammar
	 */
	public static class NumberTokenizer<G extends Grammar> extends GenericTokenizer<G, Number<G>> implements Tokenizer<G, Number<G>> {

		protected NumberTokenizer() {
			super("(\\d+)", new NumberBuilder<>());
		}

	}

	/**
	 * Typical number tokenizer, recognises integer series.
	 * 
	 * @param <G>
	 *            a grammar
	 * @return a tokenizer
	 */
	public static <G extends Grammar> NumberTokenizer<G> tokenizer() {
		return new NumberTokenizer<G>();
	}

	@Override
	public String toString() {
		return "(Number: \"" + num + "\")";
	}

	@Override
	public String get() {
		return Integer.toString(num);
	}

	@Override
	public TokenType getTokenType() {
		return TokenType.WORD;
	}

	private static class NumberBuilder<G extends Grammar> implements TokenBuilder<G, Number<G>> {
		@Override
		public Number<G> build(String str) {
			return new Number<G>(Integer.parseInt(str));
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
