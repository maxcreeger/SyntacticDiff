package tokenizer;

/** Basic types of Tokens found in code. */
public enum TokenType {
    /** Any whitespace. */
    WHITESPACE,
    /** Any word. */
    WORD,
    /** Any language-specific symbol. */
    SYMBOL,
    /** A line feed. */
    LINE_FEED,
    /** The end of the token stream. */
    END_OF_STREAM;
}
