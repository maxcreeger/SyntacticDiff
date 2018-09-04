package lexeme.java.tree.expression.statement.operators.binary;

import org.junit.Test;

import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.primitivetypes.BooleanValue;
import tokenizer.CodeLocator;

public class BooleanAndTest {
    @Test
    public void testConstructor() {
        Statement left = new BooleanValue(true, new CodeLocator("").branch().commit());
        Statement right = new BooleanValue(false, new CodeLocator("").branch().commit());
        BooleanAnd and = new BooleanAnd(left, right, new CodeLocator("").branch().commit());
    }
}
