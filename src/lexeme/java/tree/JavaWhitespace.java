package lexeme.java.tree;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.intervals.InLineComment;
import lexeme.java.intervals.LineComment;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;
import tokenizer.tokens.Whitespace;

/**
 * Ignored token representing consecutive whitespace characters and comments
 */
@Getter
public class JavaWhitespace extends Whitespace<JavaGrammar> {

    private static final Pattern whitespacePattern = Pattern.compile("^\\s+");

    private final String whitespaceContent;

    public JavaWhitespace(CodeLocation location) {
        super(location);
        whitespaceContent = location.getCode();
    }

    /**
     * Attempts to build a {@link JavaWhitespace} from the input.
     * @param input the source String
     * @return an {@link Optional} which may carry a {@link JavaWhitespace} is one was found.
     */
    public static Optional<JavaWhitespace> build(CodeBranch input) {
        CodeBranch fork = input.fork();
        Matcher matcher = whitespacePattern.matcher(fork.getRest());
        if (matcher.lookingAt()) {
            fork.advance(matcher.end());
            return Optional.of(new JavaWhitespace(fork.commit()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Helper method, mutates the input reference to skip any non-meaningful chars like whitespace and comments.
     * @param input the input reference to a String, will be mutated.
     */
    public static void skipWhitespaceAndComments(CodeBranch input) {
        CodeBranch local = input.fork();
        outer: while (true) {
            // Skip whitespace
            while (true) {
                Optional<JavaWhitespace> potentialWhitespace = JavaWhitespace.build(local);
                if (potentialWhitespace.isPresent()) {
                    continue outer;
                }
                break;
            }
            // Skip inline comment
            while (true) {
                Optional<InLineComment> potentialInLineComment = InLineComment.build(local);
                if (potentialInLineComment.isPresent()) {
                    continue outer;
                }
                break;
            }
            // Skip line comments
            while (true) {
                Optional<LineComment> potentialLineComment = LineComment.build(local);
                if (potentialLineComment.isPresent()) {
                    continue outer;
                }
                break;
            }
            // none of the above found
            break;
        }
        local.commit();
    }

}
