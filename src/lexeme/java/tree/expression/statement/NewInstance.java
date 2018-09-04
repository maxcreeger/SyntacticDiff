package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.ClassName;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.ParameterPassing;
import lombok.AllArgsConstructor;
import lombok.Getter;
import settings.SyntacticSettings;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents a call to a constructor "new Object(arguments...)".
 */
@Getter
@AllArgsConstructor
public class NewInstance extends Statement {

    private static final Pattern newPattern = Pattern.compile("new\\s+");

    private final ClassName className;
    private final ParameterPassing constructorArguments;
    private final CodeLocation location;

    public static Optional<NewInstance> build(CodeBranch input) {
        CodeBranch fork = input.fork();
        Matcher newMatcher = newPattern.matcher(fork.getRest());
        if (!newMatcher.lookingAt()) {
            return Optional.empty();
        }

        // Reserved keyword 'new' has been found
        fork.advance(newMatcher.end());
        JavaWhitespace.skipWhitespaceAndComments(fork);

        Optional<ClassName> className = ClassName.build(fork);
        if (!className.isPresent()) {
            throw new RuntimeException("Expecting Constructor after 'new' keyword");
        }

        Optional<ParameterPassing> params = ParameterPassing.build(fork);
        return Optional.of(new NewInstance(className.get(), params.get(), fork.commit()));
    }

    @Override
    public boolean isAssignable() {
        return false;
    }

    // display

    @Override
    public List<String> show(String prefix) {
        final String newFormatted = SyntacticSettings.red() + SyntacticSettings.bold() + "new" + SyntacticSettings.reset();
        final List<String> showArguments = constructorArguments.show("");
        if (showArguments.size() == 0) {
            return Arrays.asList(prefix + newFormatted + " " + className + "( )");
        } else if (showArguments.size() == 1) {
            return Arrays.asList(prefix + newFormatted + " " + className + "( " + showArguments.get(0) + " )");
        } else {
            List<String> result = new ArrayList<>();
            result.add(prefix + newFormatted + " " + className + "(");
            for (String line : showArguments) {
                result.add(prefix + "n  " + line);
            }
            return result;
        }
    }

    @Override
    public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
