package lexeme.java.tree.expression.statement.operators.binary;

import org.junit.Test;

import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.operators.binary.BooleanAnd;
import lexeme.java.tree.expression.statement.primitivetypes.BooleanValue;

public class BooleanAndTest {
    @Test
    public void testConstructor() {
        Statement left = new BooleanValue(true);
        Statement right = new BooleanValue(false);
        BooleanAnd and = new BooleanAnd(left, right);
    }
}
