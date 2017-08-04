package parser.syntaxtree.expression.statement.operators.binary;

import org.junit.Test;

import parser.syntaxtree.expression.statement.Statement;
import parser.syntaxtree.expression.statement.primitivetypes.BooleanValue;

public class BooleanAndTest {
    @Test
    public void testConstructor() {
        Statement left = new BooleanValue(true);
        Statement right = new BooleanValue(false);
        BooleanAnd and = new BooleanAnd(left, right);
    }
}
