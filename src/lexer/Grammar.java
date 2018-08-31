package lexer;

/**
 * A grammar
 */
public interface Grammar {

    /**
     * Returns an enum comprising all symbols used by this language.
     * @param <E> the type of enum
     * @return all the enum instances.
     */
    public <E extends Enum<E>> E[] getAllSymbols();

}
