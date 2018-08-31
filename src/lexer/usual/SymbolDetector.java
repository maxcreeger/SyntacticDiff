package lexer.usual;

import java.util.function.Predicate;

import lexer.java.JavaLexer.JavaGrammar;
import tokenizer.Token;
import tokenizer.tokens.Symbol;

/**
 * Detects if a {@link Token} is actually a {@link Symbol} which matches a {@link Predicate}.
 */
public class SymbolDetector extends TokenDetector<JavaGrammar, Symbol<JavaGrammar>> {

    private final Predicate<Symbol<JavaGrammar>> detector;

    /**
     * Build a {@link SymbolDetector} with a {@link Predicate}.
     * @param detector the predicate
     */
    public SymbolDetector(Predicate<Symbol<JavaGrammar>> detector) {
        super();
        this.detector = detector;
    }

    @Override
    public Boolean visit(Symbol<JavaGrammar> token) {
        return detector.test(token);
    }

}
