package lexeme.java.intervals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.IntervalToken;
import lexeme.java.tree.JavaWhitespace;
import tokenizer.CodeLocator.CodeBranch;

public class Chevron implements IntervalToken {

    private static final Pattern open = Pattern.compile("^<");
    private static final Pattern close = Pattern.compile(">");

    String contentRaw;

    /**
     * Attempts to find an open chevron '<'. <br>
     * Any subsequent whitespace is removed.
     * @param input the input string (is mutated if the chevron is found)
     * @return true if it was found
     */
    public static boolean open(CodeBranch input) {
        Matcher matcher = open.matcher(input.getRest());
        if (matcher.lookingAt()) {
            input.advance(matcher.end());
            JavaWhitespace.skipWhitespaceAndComments(input);
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
    public static boolean close(CodeBranch input) {
        Matcher matcher = close.matcher(input.getRest());
        if (matcher.lookingAt()) {
            input.advance(matcher.end());
            JavaWhitespace.skipWhitespaceAndComments(input);
            return true;
        } else {
            return false;
        }
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
