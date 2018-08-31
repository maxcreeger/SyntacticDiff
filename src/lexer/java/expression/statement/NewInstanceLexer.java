package lexer.java.expression.statement;

import java.util.Optional;

import lexeme.java.tree.ClassName;
import lexeme.java.tree.ParameterPassing;
import lexeme.java.tree.expression.statement.NewInstance;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lexer.java.JavaLexer.JavaSymbols;
import lexer.usual.structure.SequenceStructure;
import lexer.usual.structure.SequenceStructure.SequenceStructureLexer;
import tokenizer.TokenStream;

public class NewInstanceLexer implements StructureLexer<JavaGrammar, NewInstance> {

    private static final SequenceStructureLexer<JavaGrammar> SEQUENCE = new SequenceStructureLexer<JavaGrammar>("Nex Instance Lexer");
    static {
        SEQUENCE.addLexer(JavaGrammar.SINGLE_SYMBOL_CLASS, JavaSymbols.NEW.lexer);
        SEQUENCE.addLexer(ClassName.class, ClassName.LEXER);
        SEQUENCE.addLexer(ParameterPassing.class, ParameterPassing.LEXER);
    }

    @Override
    public Optional<NewInstance> lex(TokenStream<JavaGrammar> input) {
        Optional<SequenceStructure<JavaGrammar>> seq = SEQUENCE.lex(input);
        if (seq.isPresent()) {
            ClassName className = seq.get().get(ClassName.class, 1);
            ParameterPassing params = seq.get().get(ParameterPassing.class, 2);
            return Optional.of(new NewInstance(className, params));
        } else {
            return Optional.empty();
        }
    }

}
