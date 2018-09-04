package tokenizer.tokens;

import lexer.Grammar;
import lombok.Getter;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Simple implementation of generic symbol.
 *
 * @param <G> a grammar
 */
@Getter
public class SymbolImpl<G extends Grammar> implements Symbol<G> {

    final String symbol;
    final CodeLocation location;

    public SymbolImpl(CodeLocation code) {
        this.symbol = code.getCode();
        this.location = code;
    }

    @Override
    public String toString() {
        return "(Symbol: \"" + symbol + "\")";
    }

}
