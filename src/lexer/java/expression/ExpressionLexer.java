package lexer.java.expression;

import java.util.Optional;

import lexeme.java.tree.expression.Expression;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import tokenizer.TokenStream;

public class ExpressionLexer implements StructureLexer<JavaGrammar, Expression> {

    @Override
    public Optional<? extends Expression> lex(TokenStream<JavaGrammar> input) {
        // TODO Auto-generated method stub
        return null;
    }

}
