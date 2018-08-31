package lexer.java.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lexeme.java.tree.ParameterPassing;
import lexeme.java.tree.expression.statement.Statement;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lexer.java.JavaLexer.JavaSymbols;
import tokenizer.TokenStream;

public class ParameterPassingLexer implements StructureLexer<JavaGrammar, ParameterPassing> {

    @Override
    public Optional<ParameterPassing> lex(TokenStream<JavaGrammar> input) {
        TokenStream<JavaGrammar> fork = input.fork();
        if (!JavaSymbols.OPEN_PAREN.lexer.lex(fork).isPresent()) {
            return Optional.empty();
        }

        // Browse for arguments
        List<Statement> arguments = new ArrayList<>();
        while (true) {
            // Attempt to read a Statement
            Optional<? extends Statement> arg = Statement.STATEMENT_LEXER.lex(fork);
            if (!arg.isPresent()) {
                break;
            }
            arguments.add(arg.get());

            // Attempt to have a delimiter between statements?
            if (JavaSymbols.COMA.lexer.lex(fork).isPresent()) {
                continue; // Yay! another one!
            } else {
                break; // Hmm it's over
            }
        }

        // Attempt close parenthesis
        if (!JavaSymbols.CLOSE_PAREN.lexer.lex(fork).isPresent()) {
            return Optional.empty(); // Unable to close the parameter passing
        }

        // A ParameterPassing Object can be constructed, commit changes to the input and return the object
        fork.commit();
        return Optional.of(new ParameterPassing(arguments));
    }

}
