package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.JavaWhitespace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import settings.SyntacticSettings;
import tokenizer.CodeLocator.CodeBranch;

/**
 * A return statement. May return a value, or not.
 */
@Getter
@AllArgsConstructor
public class Return extends Statement {

    private static final Pattern returnPattern = Pattern.compile("return");

    private final Optional<? extends Statement> returnedValue;

    /**
     * Attempts to build a {@link Return} statement.
     * @param input the input string (mutated if a return statement is found)
     * @return optionally, a {@link Return} statement.
     */
    public static Optional<Return> build(CodeBranch input) {
        Matcher optReturn = returnPattern.matcher(input.getRest());
        if (!optReturn.lookingAt()) {
            return Optional.empty();
        }

        input.advance(optReturn.end());
        JavaWhitespace.skipWhitespaceAndComments(input);
        Optional<? extends Statement> optStatement = Statement.build(input);
        return Optional.of(new Return(optStatement));
    }

    @Override
    public boolean isAssignable() {
        return false;
    }

    @Override
    public List<String> show(String prefix) {
        List<String> returnDisplay = new ArrayList<>();
        if (returnedValue.isPresent()) {
            List<String> retValStr = returnedValue.get().show("");
            for (int i = 0; i < retValStr.size(); i++) {
                if (i == 0) {
                    returnDisplay.add(
                            prefix + SyntacticSettings.red() + SyntacticSettings.bold() + "return " + SyntacticSettings.reset() + retValStr.get(i));
                } else {
                    returnDisplay.add(prefix + "       " + retValStr.get(i));
                }
            }
        } else {
            returnDisplay.add(prefix + SyntacticSettings.red() + SyntacticSettings.bold() + "return;" + SyntacticSettings.reset());
        }
        return returnDisplay;
    }

    @Override
    public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
