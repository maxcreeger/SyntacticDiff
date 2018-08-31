package lexer.usual;

import java.util.function.Predicate;

import lexer.Grammar;
import tokenizer.Token;
import tokenizer.TokenVisitor;
import tokenizer.tokens.LineFeed;
import tokenizer.tokens.Number;
import tokenizer.tokens.Symbol;
import tokenizer.tokens.Whitespace;
import tokenizer.tokens.Word;

/**
 * A {@link TokenVisitor} that can tell if a token matches certain criteria.
 *
 * @param <G> the grammar of the tokens
 * @param <T> the tokens that can be discovered
 */
public class TokenDetector<G extends Grammar, T extends Token<G>> implements TokenVisitor<G, Boolean>, Predicate<T> {

    /**
     * Detects if the input {@link Word} matches a criteria.
     * @param token the input word
     * @return optionally, a Word token
     */
    @Override
    public Boolean visit(Word<G> token) {
        return false;
    }

    @Override
    public Boolean visit(Number<G> token) {
        return false;
    }

    @Override
    public Boolean visit(Symbol<G> token) {
        return false;
    }

    @Override
    public Boolean visit(LineFeed<G> token) {
        return false;
    }

    @Override
    public Boolean visit(Whitespace<G> token) {
        return false;
    }

    @Override
    public final Boolean defaultResult() {
        return false;
    }

    @Override
    public boolean test(T t) {
        return t.accept(this);
    }

}
