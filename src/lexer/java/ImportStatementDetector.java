package lexer.java;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lexeme.java.tree.ImportStatement;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lexer.java.JavaLexer.JavaSymbols;
import lexer.usual.structure.OptionalStructure;
import lexer.usual.structure.OptionalStructure.OptionalStructureFinder;
import lexer.usual.structure.RepeatStructure;
import lexer.usual.structure.RepeatStructure.RepeatStructureFinder;
import lexer.usual.structure.SequenceStructure;
import lexer.usual.structure.SequenceStructure.SequenceStructureLexer;
import lexer.usual.structure.SingleWordStructureLexer;
import tokenizer.TokenStream;
import tokenizer.java.JavaCodeTokenizer;
import tokenizer.tokens.Word;

/** Detects java imports like "import java.util.List;" or "import lexer.usual.structure.SingleTokenStructure.*" */
public class ImportStatementDetector implements StructureLexer<JavaGrammar, ImportStatement> {

    // Basic tokens
    private static final SingleWordStructureLexer<JavaGrammar> PACKAGE_NAME_LEXER =
            new SingleWordStructureLexer<JavaGrammar>(token -> token.get().matches("[a-z0-9]+"));
    private static final SingleWordStructureLexer<JavaGrammar> CLASS_NAME_LEXER =
            new SingleWordStructureLexer<JavaGrammar>(token -> token.get().matches("[A-Z][A-Za-z0-9]*"));

    // Sequence ".package1"
    private static final SequenceStructureLexer<JavaGrammar> DOT_PACKAGE = new SequenceStructureLexer<>("dot package finder");
    static {
        DOT_PACKAGE.addLexer(JavaGrammar.SINGLE_SYMBOL_CLASS, JavaSymbols.DOT.lexer);
        DOT_PACKAGE.addLexer(JavaGrammar.SINGLE_WORD_CLASS, PACKAGE_NAME_LEXER);
    }

    // Sequence ".Class1"
    private static final SequenceStructureLexer<JavaGrammar> DOT_CLASS = new SequenceStructureLexer<>("dot class finder");
    static {
        DOT_CLASS.addLexer(JavaGrammar.SINGLE_SYMBOL_CLASS, JavaSymbols.DOT.lexer);
        DOT_CLASS.addLexer(JavaGrammar.SINGLE_WORD_CLASS, CLASS_NAME_LEXER);
    }

    // Sequence ".*"
    private static final SequenceStructureLexer<JavaGrammar> DOT_STAR = new SequenceStructureLexer<>("dot star finder");
    static {
        DOT_STAR.addLexer(JavaGrammar.SINGLE_SYMBOL_CLASS, JavaSymbols.DOT.lexer);
        DOT_STAR.addLexer(JavaGrammar.SINGLE_SYMBOL_CLASS, JavaSymbols.STAR.lexer);
    }

    // Any number of sequences ".package1.package2" including zero sequences
    private static final StructureLexer<JavaGrammar, RepeatStructure<JavaGrammar, SequenceStructure<JavaGrammar>>> REPEAT_DOT_PACKAGE_LEXER =
            new RepeatStructureFinder<JavaGrammar, SequenceStructure<JavaGrammar>, SequenceStructureLexer<JavaGrammar>>(DOT_PACKAGE);
    // Any number of sequences ".Class1.Class2" including zero sequences
    private static final StructureLexer<JavaGrammar, RepeatStructure<JavaGrammar, SequenceStructure<JavaGrammar>>> REPEAT_DOT_CLASS_LEXER =
            new RepeatStructureFinder<JavaGrammar, SequenceStructure<JavaGrammar>, SequenceStructureLexer<JavaGrammar>>(DOT_CLASS);
    // An optional '.*' sequence
    private static final OptionalStructureFinder<JavaGrammar, SequenceStructure<JavaGrammar>> OPTIONAL_DOT_STAR_LEXER =
            new OptionalStructureFinder<JavaGrammar, SequenceStructure<JavaGrammar>>(DOT_STAR);

    // Find everything
    private static final SequenceStructureLexer<JavaGrammar> TOTAL_SEQUENCE = new SequenceStructureLexer<>("import finder");
    static {
        TOTAL_SEQUENCE.addLexer(JavaGrammar.SINGLE_SYMBOL_CLASS, JavaSymbols.IMPORT.lexer);
        TOTAL_SEQUENCE.addLexer(JavaGrammar.SINGLE_WHITESPACE_CLASS, JavaGrammar.WHITESPACE_TOKEN_LEXER);
        TOTAL_SEQUENCE.addLexer(JavaGrammar.SINGLE_WORD_CLASS, PACKAGE_NAME_LEXER); // root package
        TOTAL_SEQUENCE.addLexer(JavaGrammar.REPEAT_SEQUENCE_CLASS, REPEAT_DOT_PACKAGE_LEXER);
        TOTAL_SEQUENCE.addLexer(JavaGrammar.REPEAT_SEQUENCE_CLASS, REPEAT_DOT_CLASS_LEXER);
        TOTAL_SEQUENCE.addLexer(JavaGrammar.OPTIONAL_SEQUENCE_CLASS, OPTIONAL_DOT_STAR_LEXER);
        TOTAL_SEQUENCE.addLexer(JavaGrammar.SINGLE_SYMBOL_CLASS, JavaSymbols.SEMICOLON.lexer);
    }

    public Optional<ImportStatement> lex(TokenStream<JavaGrammar> originalTokenStream) {
        Optional<SequenceStructure<JavaGrammar>> lexemeOpt = TOTAL_SEQUENCE.lex(originalTokenStream);
        if (!lexemeOpt.isPresent()) {
            return Optional.empty();
        }
        SequenceStructure<JavaGrammar> lexeme = lexemeOpt.get();
        // Root package
        Word<JavaGrammar> rootPackage = lexeme.get(JavaGrammar.SINGLE_WORD_CLASS, 2);

        // Package structure
        RepeatStructure<JavaGrammar, SequenceStructure<JavaGrammar>> packageRepeat = lexeme.get(JavaGrammar.REPEAT_SEQUENCE_CLASS, 3);
        List<String> packageList = packageRepeat.getSequence().stream().map(seq -> seq.get(JavaGrammar.SINGLE_WORD_CLASS, 1).getToken().get())
                .collect(Collectors.toList());

        // Class structure
        RepeatStructure<JavaGrammar, SequenceStructure<JavaGrammar>> classRepeat = lexeme.get(JavaGrammar.REPEAT_SEQUENCE_CLASS, 4);
        List<String> classList = classRepeat.getSequence().stream().map(seq -> seq.get(JavaGrammar.SINGLE_WORD_CLASS, 1).getToken().get())
                .collect(Collectors.toList());
        packageList.add(0, rootPackage.getToken().get());

        // Optional star
        OptionalStructure<JavaGrammar, SequenceStructure<JavaGrammar>> optionalStar = lexeme.get(JavaGrammar.OPTIONAL_SEQUENCE_CLASS, 5);
        return Optional.of(new ImportStatement(packageList, classList, optionalStar.getOpt().isPresent()));
    }

    /**
     * Test.
     * @param argc unused
     */
    public static void main(String[] argc) {
        String longImport = "import truc.machin;";
        TokenStream<JavaGrammar> longTokenStream = TokenStream.of(JavaCodeTokenizer.tokenize(longImport).get());

        Optional<ImportStatement> longImportStructure = new ImportStatementDetector().lex(longTokenStream);
        longImportStructure.get();
    }

}
