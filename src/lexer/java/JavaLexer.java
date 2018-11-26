package lexer.java;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Predicate;

import lexeme.java.tree.Root;
import lexer.Grammar;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lexer.usual.SymbolDetector;
import lexer.usual.structure.OptionalStructure;
import lexer.usual.structure.RepeatStructure;
import lexer.usual.structure.SequenceStructure;
import lexer.usual.structure.SingleSymbolStructureLexer;
import lexer.usual.structure.SingleWhitespaceStructureLexer;
import lombok.Getter;
import tokenizer.CodeLocator;
import tokenizer.TokenStream;
import tokenizer.tokens.Symbol;
import tokenizer.tokens.SymbolImpl;
import tokenizer.tokens.Whitespace;
import tokenizer.tokens.Word;
import tokenizer.tokens.Word.WordImpl;

/** Lexes an entire Java TokenStream to produce a {@link Root} object. */
public class JavaLexer implements StructureLexer<JavaGrammar, Root> {

    /** The Java grammar. */
    public static class JavaGrammar implements Grammar {

        /** A class for single Words */
        @SuppressWarnings("unchecked")
        public static final Class<Word<JavaGrammar>> SINGLE_WORD_CLASS;
        static {
            Class<Word<JavaGrammar>> result = null;
            Type type = Word.class.getGenericInterfaces()[0];
            SINGLE_WORD_CLASS =  (Class<Word<JavaGrammar>>)type;
        }

        /** A class for single Words */
        @SuppressWarnings("unchecked")
        public static final Class<Symbol<JavaGrammar>> SINGLE_SYMBOL_CLASS =
                (Class<Symbol<JavaGrammar>>) new SymbolImpl<JavaGrammar>(null).getClass().getInterfaces()[0];

        /** A class for single Words */
        @SuppressWarnings("unchecked")
        public static final Class<Whitespace<JavaGrammar>> SINGLE_WHITESPACE_CLASS =
                (Class<Whitespace<JavaGrammar>>) new Whitespace<JavaGrammar>(new CodeLocator("toto").branch().getLocation()).getClass();

        /** A class for sequence */
        @SuppressWarnings("unchecked")
        public static final Class<SequenceStructure<JavaGrammar>> SEQUENCE_CLASS =
                (Class<SequenceStructure<JavaGrammar>>) new SequenceStructure<JavaGrammar>().getClass();

        /** A class for repeated sequence */
        @SuppressWarnings("unchecked")
        public static final Class<RepeatStructure<JavaGrammar, SequenceStructure<JavaGrammar>>> REPEAT_SEQUENCE_CLASS =
                (Class<RepeatStructure<JavaGrammar, SequenceStructure<JavaGrammar>>>) new RepeatStructure<>(null).getClass();

        /** A class for repeated sequence */
        @SuppressWarnings("unchecked")
        public static final Class<OptionalStructure<JavaGrammar, SequenceStructure<JavaGrammar>>> OPTIONAL_SEQUENCE_CLASS =
                (Class<OptionalStructure<JavaGrammar, SequenceStructure<JavaGrammar>>>) new OptionalStructure<>(null).getClass();

        /** A class for optional sequence */
        @SuppressWarnings("unchecked")
        public static final Class<OptionalStructure<JavaGrammar, RepeatStructure<JavaGrammar, SequenceStructure<JavaGrammar>>>> OPTIONAL_REPEAT_STRUCTURE_CLASS =
                (Class<OptionalStructure<JavaGrammar, RepeatStructure<JavaGrammar, SequenceStructure<JavaGrammar>>>>) new OptionalStructure<>(null)
                        .getClass();

        /** Whitespace lexer. */
        public static final SingleWhitespaceStructureLexer<JavaGrammar> WHITESPACE_TOKEN_LEXER =
                new SingleWhitespaceStructureLexer<JavaGrammar>(token -> true);

        @SuppressWarnings("unchecked")
        @Override
        public JavaSymbols[] getAllSymbols() {
            return JavaSymbols.values();
        }

    }

    @SuppressWarnings("javadoc")
    public static enum JavaSymbols {
        // Separators
        SEMICOLON("(;)", ";"), //
        COMA("(,)", ","), //
        DOT("(\\.)", "."), //
        COLON("(\\:)", ":"), //
        // Pairs
        OPEN_PAREN("(\\()", "("), //
        CLOSE_PAREN("(\\))", ")"), //
        OPEN_BRACES("(\\{)", "{"), //
        CLOSE_BRACES("(\\})", "}"), //
        OPEN_BRACKETS("(\\[)", "["), //
        CLOSE_BRACKETS("(\\])", "]"), //
        OPEN_CHEVRONS("(<)", "<"), //
        CLOSE_CHEVRONS("(>)", ">"), //
        OPEN_COMMENTS("(\\/\\*)", "/*"), //
        CLOSE_COMMENTS("(\\*\\/)", "*/"), //
        // Operators
        EQUAL("(\\=)", "="), //
        EQUAL_EQUAL("(\\=\\=)", "=="), //
        PLUS("(\\+)", "+"), //
        PLUSPLUS("(\\+\\+)", "++"), //
        MINUS("(\\-)", "-"), //
        MINUSMINUS("(\\--)", "--"), //
        STAR("(\\*)", "*"), //
        DIVIDE("(\\/)", "/"), //
        AMPERSAND("(\\&)", "&"), //
        NOT("(\\!)", "!"), //
        PIPE("(\\|)", "|"), // TODO: double pipe for shortcut eval
        MODULO("(\\%)", "%"), //
        XOR("(\\^)", "^"), //
        // Functional
        ARROW("(\\->)", "->"), //
        // Wild card
        WILDCARD("(\\?)", "?"), //
        // Strings & chars
        BACKSLASH("(\\\")", "\""), //
        SINGLE_QUOTE("(')", "'"), //
        IN_LINE_COMMENT("(\\/\\/.*)", "<LineComment>"), //
        // Class Keywords
        PACKAGE("(package)\\W", "package"), //
        IMPORT("(import)\\W", "import"), //
        CLASS("(class)\\W", "class"), //
        INTERFACE("(interface)\\W", "interface"), //
        EXTENDS("(extends)\\W", "extends"), //
        IMPLEMENTS("(implements)\\W", "implements"), //
        ABSTRACT("(abstract)\\W", "abstract"), //
        SYNCHRONIZED("(synchronized)\\W", "synchronized"), //
        PUBLIC("(public)\\W", "public"), //
        PROTECTED("(protected)\\W", "protected"), //
        PRIVATE("(private)\\W", "private"), //
        STATIC("(static)\\W", "static"), //
        FINAL("(final)\\W", "final"), //
        TRANSIENT("(transient)\\W", "transient"), //
        VOLATILE("(volatile)\\W", "volatile"), //
        // Special entities
        NEW("(new)\\W", "new"), //
        SUPER("(super)\\W", "super"), //
        THIS("(this)\\W", "this"), //
        VOID("(void)\\W", "void"), //
        NULL("(null)\\W", "null"), //
        // Booleans
        TRUE("(true)\\W", "true"), //
        FALSE("(false)\\W", "false"), //
        // Command flow
        TRY("(try)\\W", "try"), //
        CATCH("(catch)\\W", "catch"), //
        FINALLY("(finally)\\W", "finally"), //
        FOR("(for)\\W", "for"), //
        WHILE("(while)\\W", "while"), //
        DO("(do)\\W", "do"), //
        RETURN("(return)\\W", "return"), //
        CONTINUE("(continue)\\W", "continue"), //
        BREAK("(break)\\W", "break");//

        public final SymbolDetector detector;
        public final SingleSymbolStructureLexer<JavaGrammar> lexer;

        @Getter
        public final String representation;

        JavaSymbols(Predicate<Symbol<JavaGrammar>> predicate, String standardRepresentation) {
            this.detector = new SymbolDetector(predicate);
            this.representation = standardRepresentation;
            this.lexer = new SingleSymbolStructureLexer<JavaGrammar>(predicate);

        }

        JavaSymbols(String pattern, String standardRepresentation) {
            final Predicate<Symbol<JavaGrammar>> predicate = symbol -> symbol.getSymbol().matches(pattern);
            this.detector = new SymbolDetector(predicate);
            this.representation = standardRepresentation;
            this.lexer = new SingleSymbolStructureLexer<JavaGrammar>(predicate);
        }

    }

    @Override
    public Optional<? extends Root> lex(TokenStream<JavaGrammar> input) {
        // TODO Auto-generated method stub
        return null;
    }

}
