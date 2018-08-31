package tokenizer.tokens;

import lexer.Grammar;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Simple implementation of generic symbol.
 *
 * @param <G> a grammar
 */
@Getter
@AllArgsConstructor
public class SymbolImpl<G extends Grammar> implements Symbol<G> {

    String symbol;

    @Override
    public String toString() {
        return "(Symbol: \"" + symbol + "\")";
    }

}
