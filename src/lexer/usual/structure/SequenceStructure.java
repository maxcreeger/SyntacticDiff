package lexer.usual.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lexer.Grammar;
import lexer.Structure;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator;
import tokenizer.Token;
import tokenizer.TokenStream;
import tokenizer.java.JavaCodeTokenizer;
import tokenizer.tokens.Symbol;
import tokenizer.tokens.SymbolImpl;

/**
 * Represents an ordered, logical sequence of structures.
 *
 * @param <G> a {@link Grammar}
 */
public class SequenceStructure<G extends Grammar> implements Structure<G> {

    private final List<Structure<G>> sequence;
    private final Map<Integer, Class<? extends Structure<G>>> map;

    /**
     * Builds a sequence {@link Structure}. Use a {@link SequenceStructureLexer} preferably.
     */
    public SequenceStructure() {
        sequence = new ArrayList<>();
        map = new HashMap<>();
    }

    private void append(Class<? extends Structure<G>> clazz, Structure<G> structure) {
        if (!clazz.isInstance(structure)) {
            throw new IllegalArgumentException(
                    "An item of type " + structure.getClass().getName() + " was provided, but it was supposed to be a " + clazz.getName());
        }
        sequence.add(structure);
        map.put(sequence.size() - 1, clazz);
    }

    /**
     * Returns a (typed) structure at a given index in the sequence.
     * 
     * @param <S> the expected type
     * @param expectedClass the expected {@link Class} reference
     * @param index the index in the sequence
     * @return the item at the provided index, cast in the correct type
     */
    public <S extends Structure<G>> S get(Class<S> expectedClass, int index) {
        final Class<? extends Structure<G>> trueClass = map.get(index);
        if (trueClass.equals(expectedClass)) {
            return expectedClass.cast(sequence.get(index));
        } else {
            throw new IllegalArgumentException("Expected " + expectedClass.getName() + " but the stored item is a " + trueClass.getName());
        }
    }

    @Getter
    @AllArgsConstructor
    private static class LexerAndClass<G extends Grammar, S extends Structure<G>> {
        private final StructureLexer<G, S> lexer;
        private final Class<S> clazz;
    }

    /**
     * {@link StructureLexer} that can lex a sequence.
     *
     * @param <G> a {@link Grammar}
     */
    @AllArgsConstructor
    public static class SequenceStructureLexer<G extends Grammar> implements StructureLexer<G, SequenceStructure<G>> {

        private final String name;

        private final List<LexerAndClass<G, Structure<G>>> finders = new ArrayList<>();

        /**
         * Add a lexer in the sequence. The lexers will be called in-order.
         * 
         * @param <S> an expected {@link Structure} type
         * @param structureClass the class of expected structure
         * @param lexer the lexer
         */
        @SuppressWarnings("unchecked")
        public <S extends Structure<G>> void addLexer(Class<S> structureClass, StructureLexer<G, S> lexer) {
            finders.add((LexerAndClass<G, Structure<G>>) new LexerAndClass<G, S>(lexer, structureClass));
        }

        @Override
        public Optional<SequenceStructure<G>> lex(TokenStream<G> input) {
            TokenStream<G> fork = input.fork();
            SequenceStructure<G> seq = new SequenceStructure<>();
            for (LexerAndClass<G, ? extends Structure<G>> lexerAndClass : finders) {
                Optional<? extends Structure<G>> match = lexerAndClass.getLexer().lex(fork);
                if (match.isPresent()) {
                    seq.append(lexerAndClass.getClazz(), match.get());
                } else {
                    // Sequence broken
                    return Optional.empty();
                }
            }
            fork.commit();
            return Optional.of(seq);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Test.
     * 
     * @param argc unused
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] argc) {
        SequenceStructureLexer<JavaGrammar> seqFinder = new SequenceStructureLexer<>("test finder");
        StructureLexer<JavaGrammar, Symbol<JavaGrammar>> importLexer =
                new SingleSymbolStructureLexer<JavaGrammar>(token -> "import".equals(token.get()));
        Class<Symbol<JavaGrammar>> tokenClass = (Class<Symbol<JavaGrammar>>) new SymbolImpl<JavaGrammar>(null).getClass();
        seqFinder.addLexer(tokenClass, importLexer);
        final List<Token<JavaGrammar>> tokenList = JavaCodeTokenizer.TOKENIZER.tokenizeAll(new CodeLocator("import java;").branch()).get();
        final TokenStream<JavaGrammar> stream = TokenStream.of(tokenList);
        Optional<SequenceStructure<JavaGrammar>> seq = seqFinder.lex(stream);
        Symbol<JavaGrammar> importToken = seq.get().get(tokenClass, 0);
        importToken.toString();
    }
}
