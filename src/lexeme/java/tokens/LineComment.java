package lexeme.java.tokens;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LineComment {

    private static final Pattern lineComment = Pattern.compile("^\\/\\/.*\\n");

    private final String commentContent;


    public static Optional<LineComment> build(String input) {
        Matcher matcher = lineComment.matcher(input);
        if (matcher.lookingAt()) {
            return Optional.of(new LineComment(matcher.group(0)));
        } else {
            return Optional.empty();
        }
    }
}
