package tokenizer.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lexer.java.JavaLexer.JavaGrammar;
import tokenizer.CodeLocator;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.Token;
import tokenizer.Tokenizer;
import tokenizer.tokens.LineFeed;
import tokenizer.tokens.Number;
import tokenizer.tokens.Symbol;
import tokenizer.tokens.Whitespace;
import tokenizer.tokens.Word;
import tokenizer.usual.AggregateTokenizer;

/**
 * Tokenizer of Java code.
 */
public class JavaCodeTokenizer implements Tokenizer<JavaGrammar, Token<JavaGrammar>> {

    private static final List<Tokenizer<JavaGrammar, ? extends Token<JavaGrammar>>> tokenizers = new ArrayList<>();

    static {
        // Whitespace
        tokenizers.add(Whitespace.tokenizer());
        tokenizers.add(LineFeed.tokenizer());
        // Chained operators
        tokenizers.add(Symbol.tokenizer("(;)", ";"));
        tokenizers.add(Symbol.tokenizer("(,)", ","));
        tokenizers.add(Symbol.tokenizer("(\\.)", "."));
        // Pairs
        tokenizers.add(Symbol.tokenizer("(\\()", "("));
        tokenizers.add(Symbol.tokenizer("(\\))", ")"));
        tokenizers.add(Symbol.tokenizer("(\\{)", "{"));
        tokenizers.add(Symbol.tokenizer("(\\})", "}"));
        tokenizers.add(Symbol.tokenizer("(\\[)", "["));
        tokenizers.add(Symbol.tokenizer("(\\])", "]"));
        tokenizers.add(Symbol.tokenizer("(<)", "<"));
        tokenizers.add(Symbol.tokenizer("(>)", ">"));
        tokenizers.add(Symbol.tokenizer("(\\/\\*)", "/*"));
        tokenizers.add(Symbol.tokenizer("(\\*\\/)", "*/"));
        // Operators
        tokenizers.add(Symbol.tokenizer("(\\=)", "="));
        tokenizers.add(Symbol.tokenizer("(\\+)", "+"));
        tokenizers.add(Symbol.tokenizer("(\\-)", "-"));
        tokenizers.add(Symbol.tokenizer("(\\*)", "*"));
        tokenizers.add(Symbol.tokenizer("(\\/)", "/"));
        tokenizers.add(Symbol.tokenizer("(\\&)", "&"));
        tokenizers.add(Symbol.tokenizer("(\\!)", "!"));
        tokenizers.add(Symbol.tokenizer("(\\|)", "|"));
        tokenizers.add(Symbol.tokenizer("(\\:)", ":"));
        tokenizers.add(Symbol.tokenizer("(\\%)", "%"));
        tokenizers.add(Symbol.tokenizer("(\\^)", "^"));
        // Wildcard
        tokenizers.add(Symbol.tokenizer("(\\?)", "?"));
        // Strings & chars
        tokenizers.add(Symbol.tokenizer("(\\\")", "\""));
        tokenizers.add(Symbol.tokenizer("(')", "'"));
        tokenizers.add(Symbol.tokenizer("(\\/\\/.*)", "<LineComment>"));
        // Class Keywords
        tokenizers.add(Symbol.tokenizer("(package)\\W", "package"));
        tokenizers.add(Symbol.tokenizer("(import)\\W", "import"));
        tokenizers.add(Symbol.tokenizer("(class)\\W", "class"));
        tokenizers.add(Symbol.tokenizer("(interface)\\W", "interface"));
        tokenizers.add(Symbol.tokenizer("(extends)\\W", "extends"));
        tokenizers.add(Symbol.tokenizer("(implements)\\W", "implements"));
        tokenizers.add(Symbol.tokenizer("(abstract)\\W", "abstract"));
        tokenizers.add(Symbol.tokenizer("(synchronized)\\W", "synchronized"));
        tokenizers.add(Symbol.tokenizer("(public)\\W", "public"));
        tokenizers.add(Symbol.tokenizer("(protected)\\W", "protected"));
        tokenizers.add(Symbol.tokenizer("(private)\\W", "private"));
        tokenizers.add(Symbol.tokenizer("(static)\\W", "static"));
        tokenizers.add(Symbol.tokenizer("(final)\\W", "final"));
        // Special entities
        tokenizers.add(Symbol.tokenizer("(new)\\W", "new"));
        tokenizers.add(Symbol.tokenizer("(super)\\W", "super"));
        tokenizers.add(Symbol.tokenizer("(this)\\W", "this"));
        tokenizers.add(Symbol.tokenizer("(void)\\W", "void"));
        tokenizers.add(Symbol.tokenizer("(null)\\W", "null"));
        tokenizers.add(Symbol.tokenizer("(true)\\W", "true"));
        tokenizers.add(Symbol.tokenizer("(false)\\W", "false"));
        // Commands
        tokenizers.add(Symbol.tokenizer("(try)\\W", "try"));
        tokenizers.add(Symbol.tokenizer("(catch)\\W", "catch"));
        tokenizers.add(Symbol.tokenizer("(finally)\\W", "finally"));
        tokenizers.add(Symbol.tokenizer("(for)\\W", "for"));
        tokenizers.add(Symbol.tokenizer("(while)\\W", "while"));
        tokenizers.add(Symbol.tokenizer("(do)\\W", "do"));
        tokenizers.add(Symbol.tokenizer("(return)\\W", "return"));
        tokenizers.add(Symbol.tokenizer("(continue)\\W", "continue"));
        tokenizers.add(Symbol.tokenizer("(break)\\W", "break"));
        // Numbers
        tokenizers.add(Number.tokenizer());
        // Anything else
        tokenizers.add(Word.tokenizer());
    }

    public static final AggregateTokenizer<JavaGrammar, Token<JavaGrammar>> TOKENIZER =
            new AggregateTokenizer<JavaGrammar, Token<JavaGrammar>>(tokenizers);

    /**
     * Test.
     * 
     * @param argc unused
     */
    public static void main(String[] argc) {
        CodeLocator code = new CodeLocator("package tokenizer.java;\nimport java.util.ArrayList;\ndo{};");
        Optional<List<Token<JavaGrammar>>> result = TOKENIZER.tokenizeAll(code.branch());
        result.get();
    }

    @Override
    public Optional<Token<JavaGrammar>> tokenize(CodeBranch code) {
        return TOKENIZER.tokenize(code);
    }

}
