package lexeme.java.tokens;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InLineComment {

    private static final Pattern delimitedComment = Pattern.compile("^\\/\\*[\\s\\S]*?\\*\\/");
    private final String commentContent;


    public static Optional<InLineComment> build(String input) {
        Matcher matcher = delimitedComment.matcher(input);
        if (matcher.lookingAt()) {
            return Optional.of(new InLineComment(matcher.group(0)));
        } else {
            return Optional.empty();
        }
    }

}
