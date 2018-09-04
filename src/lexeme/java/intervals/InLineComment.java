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
public class InLineComment {

    private static final Pattern delimitedComment = Pattern.compile("^\\/\\*[\\s\\S]*?\\*\\/");
    private final CodeLocation location;


    public static Optional<InLineComment> build(CodeBranch input) {
        CodeBranch fork = input.fork();
        Matcher matcher = delimitedComment.matcher(fork.getRest());
        if (matcher.lookingAt()) {
            fork.advance(matcher.end());
            return Optional.of(new InLineComment(fork.commit()));
        } else {
            return Optional.empty();
        }
    }

}
