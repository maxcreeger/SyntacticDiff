package parser.tokens;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LineComment implements Token {

    private static final Pattern lineComment = Pattern.compile("^\\/\\/.*\\n");

    private final String commentContent;

    @Override
    public Matcher match(String line) {
        Matcher match = lineComment.matcher(line);
        return match;
    }

    public static Optional<LineComment> build(String input) {
        Matcher matcher = lineComment.matcher(input);
        if (matcher.lookingAt()) {
            return Optional.of(new LineComment(matcher.group(0)));
        } else {
            return Optional.empty();
        }
    }
}
