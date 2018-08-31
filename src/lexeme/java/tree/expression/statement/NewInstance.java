package lexeme.java.tree.expression.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.ClassName;
import lexeme.java.tree.JavaWhitespace;
import lexeme.java.tree.ParameterPassing;
import lexer.java.expression.statement.NewInstanceLexer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import settings.SyntacticSettings;

/**
 * Represents a call to a constructor "new Object(arguments...)".
 */
@Getter
@AllArgsConstructor
public class NewInstance extends Statement {

    private static final Pattern newPattern = Pattern.compile("new\\s+");

    public static final NewInstanceLexer LEXER = new NewInstanceLexer();

    private final ClassName className;
    private final ParameterPassing constructorArguments;

    public static Optional<NewInstance> build(AtomicReference<String> input) {
        Matcher newMatcher = newPattern.matcher(input.get());
        if (!newMatcher.lookingAt()) {
            return Optional.empty();
        }

        // Reserved keyword 'new' has been found
        input.set(input.get().substring(newMatcher.end()));
        JavaWhitespace.skipWhitespaceAndComments(input);

        Optional<ClassName> className = ClassName.build(input);
        if (!className.isPresent()) {
            throw new RuntimeException("Expecting Constructor after 'new' keyword");
        }

        Optional<ParameterPassing> params = ParameterPassing.build(input);
        return Optional.of(new NewInstance(className.get(), params.get()));
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
