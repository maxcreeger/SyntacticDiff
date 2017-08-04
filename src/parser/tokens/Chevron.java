package parser.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.syntaxtree.Whitespace;

public class Chevron implements IntervalToken {

    private static final Pattern open = Pattern.compile("^<");
    private static final Pattern close = Pattern.compile(">");

    private final List<Token> leaves = new ArrayList<>();
    String contentRaw;

    /**
     * Attempts to find an open chevron '<'. <br>
     * Any subsequent whitespace is removed.
     * @param input the input string (is mutated if the chevron is found)
     * @return true if it was found
     */
    public static boolean open(AtomicReference<String> input) {
        Matcher matcher = open.matcher(input.get());
        if (matcher.lookingAt()) {
            input.set(input.get().substring(matcher.end()));
            Whitespace.skipWhitespaceAndComments(input);
            return true;
        } else {
            return false;
        }
    }


    /**
     * Attempts to find an closing chevron '>'. <br>
     * Any subsequent whitespace is removed.
     * @param input the input string (is mutated if the chevron is found)
     * @return true if it was found
     */
    public static boolean close(AtomicReference<String> input) {
        Matcher matcher = close.matcher(input.get());
        if (matcher.lookingAt()) {
            input.set(input.get().substring(matcher.end()));
            Whitespace.skipWhitespaceAndComments(input);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Matcher match(String line) {
        return open.matcher(line);
    }

    @Override
    public Matcher nextClosingToken(String input) {
        return close.matcher(input);
    }

    @Override
    public void actualClosingToken(String input, int length) {
        contentRaw = input.substring(0, length);
    }

    @Override
    public int end() {
        return contentRaw.length();
    }


}