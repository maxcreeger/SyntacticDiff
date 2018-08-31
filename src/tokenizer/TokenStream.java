package tokenizer;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lexer.Grammar;
import lexer.java.JavaLexer.JavaGrammar;
import tokenizer.java.JavaCodeTokenizer;

/**
 * Represents a Stream of {@link Token}s, in the order with which they were found in a source file.<br>
 * The stream can be read using {@link #next()} <br>
 * The stream can be forked using {@link #fork()} to attempt matching a specific sequence. The fork can then be:
 * <ul>
 * <li>Committed using {@link #commit()}, then the parent stream's index advances to match the fork advancement</li>
 * <li>rolled back by simply letting the reference to it be cleaned by the Garbage Collector</li>
 * </ul>
 *
 * @param <G> a Grammar to make sense of the Tokens' meaning
 */
public interface TokenStream<G extends Grammar> {

    /**
     * Advances the index and returns the current {@link Token}.
     * @return the {@link Token}. May be null in case we reached the end of the stream.
     */
    Token<G> next();

    /**
     * Commits the advance of this fork to the parent (if any).
     * @return the parent {@link TokenStream}
     */
    TokenStream<G> commit();

    /**
     * Forks the stream. After being iterated upon, the returned fork can be committed (this instance will advance accordingly) or simply let go (this instance will be left untouched).
     * @return the fork
     */
    TokenStream<G> fork();

    /**
     * Tells if the stream contains further elements
     * @return a {@link Boolean}
     */
    boolean hasNext();

    /**
     * Initiates a fork-able {@link TokenStream} from a list of {@link Token}s.
     * @param <G> a grammar
     * @param initialStream the list
     * @return the stream
     */
    public static <G extends Grammar> TokenStream<G> of(List<Token<G>> initialStream) {
        return new TokenStreamRoot<>(new LinkedList<>(initialStream)).firstFork();
    }

    /**
     * The Root stream. This is not a {@link Fork}.
     *
     * @param <G> the {@link Grammar}
     */
    class TokenStreamRoot<G extends Grammar> {

        private final LinkedList<Token<G>> stream;
        private final Fork firstFork;

        public TokenStreamRoot(LinkedList<Token<G>> initialStream) {
            this.stream = initialStream;
            this.firstFork = new Fork(null);
        }

        private Fork firstFork() {
            return firstFork;
        }

        @Override
        public String toString() {
            return String.join("", stream.stream().map(token -> token.toString()).collect(Collectors.toList()));
        }

        private class Fork implements TokenStream<G> {

            private final Fork parent; // if null, this is root
            private int forkIndex = 0;

            public Fork(Fork parent) {
                this.parent = parent;
            }

            private void fastForward(int delta) {
                forkIndex += delta;
            }

            protected int getTotalIndex() {
                return forkIndex + (parent == null ? 0 : parent.getTotalIndex());
            }

            @Override
            public Token<G> next() {
                if (!hasNext()) {
                    return null;
                }
                final Token<G> token = stream.get(getTotalIndex());
                forkIndex++;
                return token;
            }

            @Override
            public Fork commit() {
                parent.fastForward(forkIndex);
                return parent;
            }

            @Override
            public TokenStream<G> fork() {
                return new Fork(this);
            }

            @Override
            public boolean hasNext() {
                return getTotalIndex() < stream.size();
            }


            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                for (int i = getTotalIndex(); i < stream.size(); i++) {
                    builder.append(stream.get(i));
                }
                return builder.toString();
            }
        }
    }

    /**
     * Test.
     * @param argc unused
     */
    public static void main(String[] argc) {
        Optional<List<Token<JavaGrammar>>> tokens = JavaCodeTokenizer.tokenize("package java.com.truc;");
        TokenStream<JavaGrammar> stream = TokenStream.of(tokens.get());

        // Use once, commit
        stream = stream.fork();
        Token<JavaGrammar> t1 = stream.next();
        t1.toString();
        stream = stream.commit();

        // Use once, revert
        TokenStream<JavaGrammar> fork1 = stream.fork();
        Token<JavaGrammar> t2 = fork1.next();
        t2.toString();
    }

}
