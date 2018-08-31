package lexer.java.expression.statement;

import java.util.Optional;

import lexeme.java.tree.expression.statement.NewInstance;
import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.operators.unary.prefix.PrefixUnaryOperator;
import lexeme.java.tree.expression.statement.primitivetypes.PrimitiveValue;
import lexer.StructureLexer;
import lexer.java.JavaLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lexer.java.JavaLexer.JavaSymbols;
import lexer.usual.structure.AlternativeStructure.AlternativeStructureFinder;
import lexer.usual.structure.SequenceStructure;
import lexer.usual.structure.SequenceStructure.SequenceStructureLexer;
import tokenizer.TokenStream;

public class StatementLexer implements StructureLexer<JavaGrammar, Statement> {

    private static final SequenceStructureLexer<JavaGrammar> PARENTHESIS_GROUP = new SequenceStructureLexer<>("Parenthesis grouping");

    static {
        PARENTHESIS_GROUP.addLexer(JavaGrammar.SINGLE_SYMBOL_CLASS, JavaSymbols.OPEN_PAREN.lexer);
        PARENTHESIS_GROUP.addLexer(Statement.class, Statement.STATEMENT_LEXER);
        PARENTHESIS_GROUP.addLexer(JavaGrammar.SINGLE_SYMBOL_CLASS, JavaSymbols.CLOSE_PAREN.lexer);
    }

    private static final AlternativeStructureFinder<JavaGrammar, Statement> ALTERNATIVES = new AlternativeStructureFinder<>();
    static {
        ALTERNATIVES.addAlternative(PrefixUnaryOperator.STATEMENT_LEXER);
        ALTERNATIVES.addAlternative(new StructureLexer<JavaLexer.JavaGrammar, Statement>() {

            @Override
            public Optional<? extends Statement> lex(TokenStream<JavaLexer.JavaGrammar> input) {
                // TODO Auto-generated method stub
                Optional<SequenceStructure<JavaGrammar>> paren = PARENTHESIS_GROUP.lex(input);
                if (paren.isPresent()) {
                    return Optional.of(paren.get().get(Statement.class, 1));
                } else {
                    return Optional.empty();
                }
            }
        });
        ALTERNATIVES.addAlternative(PrimitiveValue.LEXER);
        ALTERNATIVES.addAlternative(NewInstance.LEXER);

        // TODO Try a reference to this or to super
        // TODO Try straight method invocation
        // TODO Try variable reference

    }

    @Override
    public Optional<? extends Statement> lex(TokenStream<JavaGrammar> input) {
        return ALTERNATIVES.lex(input);
    }

}
