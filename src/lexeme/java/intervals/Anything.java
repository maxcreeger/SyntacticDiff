package lexeme.java.intervals;

import java.util.regex.Pattern;

public class Anything {


    private Pattern any;

    public Anything(String input, int length) {
        any = Pattern.compile("[\\s\\S]{" + length + "}");
    }

    public void resize(String shrinking, int length) {
        any = Pattern.compile("[\\s\\S]{" + length + "}");
    }

}
