package lexeme.java.tree;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tokens.InLineComment;
import lexeme.java.tokens.LineComment;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.tokens.Whitespace;

/**
 * Ignored token representing consecutive whitespace characters and comments
 */
@AllArgsConstructor
@Getter
public class JavaWhitespace extends Whitespace<JavaGrammar> {

    private static final Pattern whitespacePattern = Pattern.compile("^\\s+");

    private final String whitespaceContent;

    /**
     * Attempts to build a {@link JavaWhitespace} from the input.
     * @param input the source String
     * @return an {@link Optional} which may carry a {@link JavaWhitespace} is one was found.
     */
    public static Optional<JavaWhitespace> build(String input) {
        Matcher matcher = whitespacePattern.matcher(input);
        if (matcher.lookingAt()) {
            return Optional.of(new JavaWhitespace(matcher.group(0)));
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
                Optional<JavaWhitespace> potentialWhitespace = JavaWhitespace.build(workingInput);
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
