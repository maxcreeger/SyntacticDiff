package parser.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Anything implements Token {

    private final List<Token> leaves = new ArrayList<>();

    private Pattern any;

    public Anything(String input, int length) {
        any = Pattern.compile("[\\s\\S]{" + length + "}");
    }

    @Override
    public Matcher match(String line) {
        return any.matcher(line);
    }

    public void resize(String shrinking, int length) {
        any = Pattern.compile("[\\s\\S]{" + length + "}");
    }

}
