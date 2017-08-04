package parser.tokens;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.syntaxtree.Whitespace;

public class Parenthesis implements IntervalToken {

    private static final Pattern openPattern = Pattern.compile("^\\(");
    private static final Pattern closePattern = Pattern.compile("\\)");

    /**
     * Attempts to find an open parenthesis '('. <br>
     * Any subsequent whitespace is removed.
     * @param defensiveCopy the input string (is mutated if the parenthesis is found)
     * @return true if it was found
     */
    public static boolean open(AtomicReference<String> defensiveCopy) {
        Matcher open = openPattern.matcher(defensiveCopy.get());
        if (!open.lookingAt()) {
            return false;
        }
        defensiveCopy.set(defensiveCopy.get().substring(open.end()));
        Whitespace.skipWhitespaceAndComments(defensiveCopy);
        return true;
    }

    /**
     * Attempts to find a closing parenthesis ')'. <br>
     * Any subsequent whitespace is removed.
     * @param defensiveCopy the input string (is mutated if the parenthesis is found)
     * @return true if it was found
     */
    public static boolean close(AtomicReference<String> defensiveCopy) {
        Matcher open = closePattern.matcher(defensiveCopy.get());
        if (!open.lookingAt()) {
            return false;
        }
        defensiveCopy.set(defensiveCopy.get().substring(open.end()));
        Whitespace.skipWhitespaceAndComments(defensiveCopy);
        return true;
    }

    @Override
    public Matcher match(String file) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Matcher nextClosingToken(String input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void actualClosingToken(String input, int length) {
        // TODO Auto-generated method stub

    }

    @Override
    public int end() {
        // TODO Auto-generated method stub
        return 0;
    }

}
