package lexeme.java.intervals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import tokenizer.CodeLocator.CodeBranch;

public class Curvy {

    private static final Pattern open = Pattern.compile("\\{");
    private static final Pattern close = Pattern.compile("\\}");

    /**
     * Attempts to find an open curvy bracket '{'. <br>
     * Any subsequent whitespace is removed.
     * @param input the input string (is mutated if the bracket is found)
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
     * Attempts to find a closing curvy bracket '}'. <br>
     * Any subsequent whitespace is removed.
     * @param input the input string (is mutated if the bracket is found)
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


}

