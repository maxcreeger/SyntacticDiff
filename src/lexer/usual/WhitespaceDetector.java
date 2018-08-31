package lexer.usual;

import lexer.java.JavaLexer.JavaGrammar;
import tokenizer.Token;
import tokenizer.tokens.Whitespace;

/**
 * Detects if a {@link Token} is actually a {@link Whitespace}.
 */

public class WhitespaceDetector extends TokenDetector<JavaGrammar, Whitespace<JavaGrammar>> {

    @Override
    public Boolean visit(Whitespace<JavaGrammar> token) {
        return true;
    }

}
