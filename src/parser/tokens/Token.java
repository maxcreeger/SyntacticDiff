package parser.tokens;

import java.util.regex.Matcher;

/**
 * Represents a Java symbol. Could a class name, an operator...
 */
public interface Token {

    /**
     * Attempts to find this token in the provided String.
     * @param file the String to scan.
     * @return a {@link Matcher} which may or may not contain a match.
     */
    Matcher match(String file);

}
