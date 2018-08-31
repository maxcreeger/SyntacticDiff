package lexer.usual.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lexer.Grammar;
import lexer.Structure;
import lexer.StructureLexer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.TokenStream;

/**
 * A structure representing alternatives of other structure. Can A, B, or C.
 *
 * @param <G> a {@link Grammar}
 * @param <S> the alternative structure's common root type
 */
@AllArgsConstructor
@Getter
public class AlternativeStructure<G extends Grammar, S extends Structure<G>> implements Structure<G> {

    private final S structure;

    /**
     * Constructs {@link AlternativeStructure}s given alternative {@link StructureLexer}s.
     *
     * @param <G> a {@link Grammar}
     * @param <S> the alternative structure's common root type
     */
    @AllArgsConstructor
    public static class AlternativeStructureFinder<G extends Grammar, S extends Structure<G>> implements StructureLexer<G, S> {

        private final List<StructureLexer<G, ? extends S>> finders = new ArrayList<>();

        /**
         * Builds an {@link AlternativeStructure.AlternativeStructureFinder} given a series of alternative {@link StructureLexer}s
         * @param finders the {@link StructureLexer}s
         */
        public void addAlternative(StructureLexer<G, ? extends S> finder) {
            this.finders.add(finder);
        }

        @Override
        public Optional<S> lex(TokenStream<G> input) {
            TokenStream<G> fork = input.fork();
            for (StructureLexer<G, ? extends S> finder : finders) {
                Optional<? extends S> match = finder.lex(fork);
                if (match.isPresent()) {
                    fork.commit();
                    return Optional.of(match.get());
                }
            }
            return Optional.empty();
        }

    }

}
