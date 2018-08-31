package lexer.usual;

import java.util.function.Predicate;

import lexer.java.JavaLexer.JavaGrammar;
import tokenizer.Token;
import tokenizer.tokens.Word;

/**
 * Detects if a {@link Token} is actually a {@link Word} which matches a {@link Predicate}.
 */

public class WordDetector extends TokenDetector<JavaGrammar, Word<JavaGrammar>> {

    private final Predicate<Word<JavaGrammar>> detector;

    /**
     * Build a {@link WordDetector} with a {@link Predicate}.
     * @param detector the predicate
     */
    public WordDetector(Predicate<Word<JavaGrammar>> detector) {
        super();
        this.detector = detector;
    }

    @Override
    public Boolean visit(Word<JavaGrammar> token) {
        return detector.test(token);
    }

}
