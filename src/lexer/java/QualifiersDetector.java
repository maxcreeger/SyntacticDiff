package lexer.java;

import java.util.Optional;

import lexeme.java.tree.Qualifiers;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lexer.java.JavaLexer.JavaSymbols;
import lexer.usual.structure.AlternativeStructure.AlternativeStructureFinder;
import tokenizer.TokenStream;
import tokenizer.tokens.Symbol;

/**
 * Detects a Qualifier.
 */
public class QualifiersDetector implements StructureLexer<JavaGrammar, Qualifiers> {

    private static final AlternativeStructureFinder<JavaGrammar, Symbol<JavaGrammar>> ALTERNATIVE_LEXERS =
            new AlternativeStructureFinder<JavaGrammar, Symbol<JavaGrammar>>();

    static {
        ALTERNATIVE_LEXERS.addAlternative(JavaSymbols.PRIVATE.lexer);
        ALTERNATIVE_LEXERS.addAlternative(JavaSymbols.PROTECTED.lexer);
        ALTERNATIVE_LEXERS.addAlternative(JavaSymbols.PUBLIC.lexer);
        ALTERNATIVE_LEXERS.addAlternative(JavaSymbols.PACKAGE.lexer);
        ALTERNATIVE_LEXERS.addAlternative(JavaSymbols.STATIC.lexer);
        ALTERNATIVE_LEXERS.addAlternative(JavaSymbols.ABSTRACT.lexer);
        ALTERNATIVE_LEXERS.addAlternative(JavaSymbols.FINAL.lexer);
        ALTERNATIVE_LEXERS.addAlternative(JavaSymbols.TRANSIENT.lexer);
        ALTERNATIVE_LEXERS.addAlternative(JavaSymbols.VOLATILE.lexer);
        ALTERNATIVE_LEXERS.addAlternative(JavaSymbols.SYNCHRONIZED.lexer);
    }

    @Override
    public Optional<? extends Qualifiers> lex(TokenStream<JavaGrammar> input) {
        Optional<Symbol<JavaGrammar>> qualifier = ALTERNATIVE_LEXERS.lex(input);
        if (!qualifier.isPresent()) {
            return Optional.empty();
        }
        return Optional.of((Qualifiers) qualifier.get());
    }

}
