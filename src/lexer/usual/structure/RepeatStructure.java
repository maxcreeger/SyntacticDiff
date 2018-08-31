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
 * Constructs a structure by looking a repetitive sub-structures.
 *
 * @param <G> a {@link Grammar}
 * @param <S> the type of repeated {@link Structure}
 */
@AllArgsConstructor
@Getter
public class RepeatStructure<G extends Grammar, S extends Structure<G>> implements Structure<G> {

    private final List<S> sequence;

    /**
     * Tells if the structure was found at least one, or not.
     * @return a boolean
     */
    public boolean hasAnyRepetition() {
        return !sequence.isEmpty();
    }

    /**
     * Tells the number of times the structure was found
     * @return an integer
     */
    public int nbRepetitions() {
        return sequence.size();
    }

    /**
     * Finds a repeated {@link Structure} by repeatedly applying a {@link StructureLexer}.
     *
     * @param <G> a {@link Grammar}
     * @param <S> the type of repeated {@link Structure} that is sought
     * @param <SL> the type of {@link StructureLexer} that will be used repeatedly
     */
    @AllArgsConstructor
    public static class RepeatStructureFinder<G extends Grammar, S extends Structure<G>, SL extends StructureLexer<G, S>>
            implements
                StructureLexer<G, RepeatStructure<G, S>> {

        private final SL finder;

        @Override
        public Optional<? extends RepeatStructure<G, S>> lex(TokenStream<G> input) {
            TokenStream<G> fork = input.fork();
            List<S> found = new ArrayList<>();
            while (true) {
                Optional<? extends S> match = finder.lex(fork);
                if (match.isPresent()) {
                    found.add(match.get());
                } else {
                    break;
                }
            }
            fork.commit();
            return Optional.of(new RepeatStructure<G, S>(found));
        }
    }
}
