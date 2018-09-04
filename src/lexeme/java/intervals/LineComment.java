package lexeme.java.intervals;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

@AllArgsConstructor
@Getter
public class LineComment {

    private static final Pattern lineComment = Pattern.compile("^\\/\\/.*\\n");

    private final String commentContent;
    private final CodeLocation location;

    public static Optional<LineComment> build(CodeBranch input) {
        CodeBranch fork = input.fork();
        Matcher matcher = lineComment.matcher(fork.getRest());
        if (matcher.lookingAt()) {
            fork.advance(matcher.end());
            return Optional.of(new LineComment(fork.getContent(), fork.commit()));
        } else {
            return Optional.empty();
        }
    }
}
