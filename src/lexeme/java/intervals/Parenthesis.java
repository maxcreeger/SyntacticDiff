package lexeme.java.intervals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.IntervalToken;
import lexeme.java.tree.JavaWhitespace;
import tokenizer.CodeLocator.CodeBranch;

public class Parenthesis implements IntervalToken {

    private static final Pattern openPattern = Pattern.compile("^\\(");
    private static final Pattern closePattern = Pattern.compile("\\)");

    /**
     * Attempts to find an open parenthesis '('. <br>
     * Any subsequent whitespace is removed.
     * @param code the input string (is mutated if the parenthesis is found)
     * @return true if it was found
     */
    public static boolean open(CodeBranch code) {
        Matcher open = openPattern.matcher(code.getRest());
        if (!open.lookingAt()) {
            return false;
        }
        code.advance(open.end());
        JavaWhitespace.skipWhitespaceAndComments(code);
        return true;
    }

    /**
     * Attempts to find a closing parenthesis ')'. <br>
     * Any subsequent whitespace is removed.
     * @param code the input string (is mutated if the parenthesis is found)
     * @return true if it was found
     */
    public static boolean close(CodeBranch code) {
        Matcher open = closePattern.matcher(code.getRest());
        if (!open.lookingAt()) {
            return false;
        }
        code.advance(open.end());
        JavaWhitespace.skipWhitespaceAndComments(code);
        return true;
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
