package lexer.java.expression.statement.primitivetypes;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexeme.java.tree.expression.statement.primitivetypes.BooleanValue;
import lexeme.java.tree.expression.statement.primitivetypes.CharValue;
import lexeme.java.tree.expression.statement.primitivetypes.DoubleValue;
import lexeme.java.tree.expression.statement.primitivetypes.IntegerValue;
import lexeme.java.tree.expression.statement.primitivetypes.NullValue;
import lexeme.java.tree.expression.statement.primitivetypes.PrimitiveValue;
import lexeme.java.tree.expression.statement.primitivetypes.StringValue;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lexer.usual.structure.AlternativeStructure.AlternativeStructureFinder;
import lexer.usual.structure.SingleWordStructureLexer;
import tokenizer.TokenStream;
import tokenizer.tokens.Word;

/**
 * Lexer for primitive literals
 */
public class PrimitiveLexer extends AlternativeStructureFinder<JavaGrammar, PrimitiveValue> {

    private static final Pattern numPattern1 = Pattern.compile("(-?[0-9]+\\.[0-9]*(e-?[0-9]+)?)([dDfF]?)");
    private static final Pattern numPattern2 = Pattern.compile("(-?\\.[0-9]+(e-?[0-9]+)?)([dDfF]?)");
    private static final Pattern decimalPattern = Pattern.compile("(-?[0-9]+)([Ll]?)");
    private static final Pattern charPattern = Pattern.compile("'(\\\\?.)'");

    private static final Pattern stringPattern = Pattern.compile("\\\"[\\s\\S]*?([^\\\\]?\\\")");
    private static final Pattern quotePattern = Pattern.compile("\\\"");
    private static final Pattern endOfStringPattern = Pattern.compile("[^\\\\]?\"");
    private static final Pattern endOfLinePattern = Pattern.compile("\\n");

    private static final GenericPrimitiveValueLexer<NullValue> NULL_LEXER =
            new GenericPrimitiveValueLexer<NullValue>(word -> "null".equals(word.getWord()), word -> new NullValue());

    private static final GenericPrimitiveValueLexer<BooleanValue> BOOLEAN_LEXER = new GenericPrimitiveValueLexer<BooleanValue>(
            word -> "true".equals(word.getWord()) || "false".equals(word.getWord()), word -> new BooleanValue("true".equals(word.getWord())));

    private static final GenericPrimitiveValueLexer<IntegerValue> INTEGER_LEXER = new GenericPrimitiveValueLexer<IntegerValue>(
            word -> decimalPattern.matcher(word.getWord()).matches(), new PrimitiveBuilder<IntegerValue>() {

                @Override
                public IntegerValue build(Word<JavaGrammar> word) {
                    Matcher stringMatcher = decimalPattern.matcher(word.getWord());
                    if (stringMatcher.matches()) {
                        int val = Integer.parseInt(stringMatcher.group(1));
                        boolean asLong = !stringMatcher.group(2).isEmpty();
                        return new IntegerValue(val, asLong);
                    } else {
                        throw new RuntimeException("Failed to parse the same int value again");
                    }
                }
            });

    private static final GenericPrimitiveValueLexer<DoubleValue> DOUBLE_LEXER = new GenericPrimitiveValueLexer<DoubleValue>(
            word -> decimalPattern.matcher(word.getWord()).matches(), new PrimitiveBuilder<DoubleValue>() {

                @Override
                public DoubleValue build(Word<JavaGrammar> word) {
                    Matcher stringMatcher = numPattern1.matcher(word.getWord());
                    if (stringMatcher.lookingAt()) {
                        double val = Double.parseDouble(stringMatcher.group(0));
                        boolean isDouble = stringMatcher.group(2).isEmpty() || stringMatcher.group(2).toLowerCase().equals("d");
                        return new DoubleValue(val, isDouble);
                    }
                    stringMatcher = numPattern2.matcher(word.getWord());
                    if (stringMatcher.lookingAt()) {
                        double val = Double.parseDouble(stringMatcher.group(0));
                        boolean isDouble = stringMatcher.group(2).isEmpty() || stringMatcher.group(2).toLowerCase().equals("d");
                        return new DoubleValue(val, isDouble);
                    } else {
                        throw new RuntimeException("Failed to parse the same double value again");
                    }
                }
            });

    private static final GenericPrimitiveValueLexer<CharValue> CHAR_LEXER =
            new GenericPrimitiveValueLexer<CharValue>(word -> decimalPattern.matcher(word.getWord()).matches(), new PrimitiveBuilder<CharValue>() {

                @Override
                public CharValue build(Word<JavaGrammar> word) {
                    Matcher stringMatcher = charPattern.matcher(word.getWord());
                    if (stringMatcher.lookingAt()) {
                        return new CharValue(stringMatcher.group(1));
                    } else {
                        throw new RuntimeException("Failed to parse the same char value again");
                    }
                }
            });

    private static final GenericPrimitiveValueLexer<StringValue> STRING_LEXER = new GenericPrimitiveValueLexer<StringValue>(
            word -> decimalPattern.matcher(word.getWord()).matches(), new PrimitiveBuilder<StringValue>() {

                @Override
                public StringValue build(Word<JavaGrammar> word) {
                    Matcher stringMatcher = stringPattern.matcher(word.getWord());
                    if (stringMatcher.lookingAt()) {
                        String stringWithQuotes = stringMatcher.group();
                        return new StringValue(stringWithQuotes.substring(1, stringWithQuotes.length() - 1));
                    } else {
                        throw new RuntimeException("Failed to parse the same string litteral value again");
                    }
                }
            });

    private static class GenericPrimitiveValueLexer<T extends PrimitiveValue> implements StructureLexer<JavaGrammar, T> {

        private final SingleWordStructureLexer<JavaGrammar> wordLexer;
        private final Predicate<Word<JavaGrammar>> primitiveDetector;
        private final PrimitiveBuilder<T> builder;

        public GenericPrimitiveValueLexer(Predicate<Word<JavaGrammar>> wordDetector, PrimitiveBuilder<T> builder) {
            this.wordLexer = new SingleWordStructureLexer<>(word -> true);
            this.primitiveDetector = wordDetector;
            this.builder = builder;
        }

        @Override
        public Optional<? extends T> lex(TokenStream<JavaGrammar> input) {
            TokenStream<JavaGrammar> fork = input.fork();
            Optional<Word<JavaGrammar>> word = wordLexer.lex(fork);
            if (word.isPresent() && primitiveDetector.test(word.get())) {
                fork.commit();
                return Optional.of(builder.build(word.get()));
            } else {
                return Optional.empty();
            }
        }
    }

    @FunctionalInterface
    static interface PrimitiveBuilder<T extends PrimitiveValue> {
        T build(Word<JavaGrammar> word);
    }

    public PrimitiveLexer() {
        super();
        super.addAlternative(NULL_LEXER);
        super.addAlternative(BOOLEAN_LEXER);
        super.addAlternative(INTEGER_LEXER);
        super.addAlternative(DOUBLE_LEXER);
        super.addAlternative(CHAR_LEXER);
        super.addAlternative(STRING_LEXER);
    }

}
