package parser.tokens;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A String spanning several characters. Includes escaped <"> in the String.
 */
public class StringSpan implements Token {

    Pattern delimitedString = Pattern.compile("\"[\\s\\S]*?\"");

    @Override
    public Matcher match(String line) {
        return delimitedString.matcher(line);
    }


}
