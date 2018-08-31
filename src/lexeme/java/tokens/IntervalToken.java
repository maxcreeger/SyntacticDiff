package lexeme.java.tokens;

import java.util.regex.Matcher;

/**
 * Represents any matching pairs of tokens, such as <code>{...}</code> or <code>[...]</code>.
 */
public interface IntervalToken {

    /**
     * Finds the closing token.
     * @param input
     * @return a {@link Matcher}
     */
    Matcher nextClosingToken(String input);

    void actualClosingToken(String input, int length);

    int end();
}
