package parser.syntaxtree;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.tokens.InLineComment;
import parser.tokens.LineComment;

/**
 * Ignored token representing consecutive whitespace characters and comments
 */
@AllArgsConstructor
@Getter
public class Whitespace {

    private static final Pattern whitespacePattern = Pattern.compile("^\\s+");

    private final String whitespaceContent;

    /**
     * Attempts to build a {@link Whitespace} from the input.
     * @param input the source String
     * @return an {@link Optional} which may carry a {@link Whitespace} is one was found.
     */
    public static Optional<Whitespace> build(String input) {
        Matcher matcher = whitespacePattern.matcher(input);
        if (matcher.lookingAt()) {
            return Optional.of(new Whitespace(matcher.group(0)));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Helper method, mutates the input reference to skip any non-meaningful chars like whitespace and comments.
     * @param input the input reference to a String, will be mutated.
     */
    public static void skipWhitespaceAndComments(AtomicReference<String> input) {
        String workingInput = input.get();
        int advance = 0;
        outer: while (true) {
            // Skip whitespace
            while (true) {
                Optional<Whitespace> potentialWhitespace = Whitespace.build(workingInput);
                if (potentialWhitespace.isPresent()) {
                    advance += potentialWhitespace.get().getWhitespaceContent().length();
                    workingInput = input.get().substring(advance);
                    continue outer;
                }
                break;
            }
            // Skip inline comment
            while (true) {
                Optional<InLineComment> potentialInLineComment = InLineComment.build(workingInput);
                if (potentialInLineComment.isPresent()) {
                    advance += potentialInLineComment.get().getCommentContent().length();
                    workingInput = input.get().substring(advance);
                    continue outer;
                }
                break;
            }
            // Skip line comments
            while (true) {
                Optional<LineComment> potentialLineComment = LineComment.build(workingInput);
                if (potentialLineComment.isPresent()) {
                    advance += potentialLineComment.get().getCommentContent().length();
                    workingInput = input.get().substring(advance);
                    continue outer;
                }
                break;
            }
            // none of the above found
            break;
        }
        input.set(input.get().substring(advance));
    }

}
