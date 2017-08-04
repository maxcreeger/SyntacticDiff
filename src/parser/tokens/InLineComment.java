package parser.tokens;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InLineComment implements Token {

    private static final Pattern delimitedComment = Pattern.compile("^\\/\\*[\\s\\S]*?\\*\\/");
    private final String commentContent;

    @Override
    public Matcher match(String line) {
        return delimitedComment.matcher(line);
    }

    public static Optional<InLineComment> build(String input) {
        Matcher matcher = delimitedComment.matcher(input);
        if (matcher.lookingAt()) {
            return Optional.of(new InLineComment(matcher.group(0)));
        } else {
            return Optional.empty();
        }
    }

}
