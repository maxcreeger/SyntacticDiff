package lexer.usual.structure;

import java.util.Optional;

import lexer.Grammar;
import lexer.Structure;
import lexer.StructureLexer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.TokenStream;

/**
 * Represents a structure which may or may not have been found
 *
 * @param <G> a {@link Grammar}
 * @param <S> the type of optional {@link Structure} contained
 */
@Getter
@AllArgsConstructor
public class OptionalStructure<G extends Grammar, S extends Structure<G>> implements Structure<G> {

    private final Optional<? extends Structure<G>> opt;

    /**
     * Builds an {@link OptionalStructure} from tokens.
     *
     * @param <G> a {@link Grammar}
     * @param <S> the type of optional {@link Structure} that is sought
     */
    public static class OptionalStructureFinder<G extends Grammar, S extends Structure<G>> implements StructureLexer<G, OptionalStructure<G, S>> {

        private final StructureLexer<G, S> finder;

        /**
         * Construct an {@link OptionalStructure.OptionalStructureFinder} given a {@link StructureLexer}.
         * @param finder the lexer
         */
        public OptionalStructureFinder(StructureLexer<G, S> finder) {
            this.finder = finder;
        }

        @Override
        public Optional<OptionalStructure<G, S>> lex(TokenStream<G> input) {
            TokenStream<G> fork = input.fork();
            Optional<? extends Structure<G>> match = finder.lex(fork);
            if (match.isPresent()) {
                fork.commit();
                return Optional.of(new OptionalStructure<>(match));
            } else {
                return Optional.empty();
            }
        }

    }

}
