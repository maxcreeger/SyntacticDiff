package lexeme.java.tree.expression.statement.primitivetypes;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

import lexeme.java.tree.expression.statement.primitivetypes.BooleanValue;

public class BooleanValueTest {

    @Test
    public void testConstructorTrue() {
        BooleanValue val = new BooleanValue(false);
        Assert.assertFalse(val.isTrue());
    }

    @Test
    public void testBuildFailed() {
        AtomicReference<String> ref = new AtomicReference<>("treueue;rest");
        Optional<BooleanValue> booleanValue = BooleanValue.build(ref);
        Assert.assertFalse(booleanValue.isPresent());
        Assert.assertEquals(ref.get(), "treueue;rest");
    }

    @Test
    public void testBuildTrue() {
        AtomicReference<String> ref = new AtomicReference<>("true;rest");
        Optional<BooleanValue> booleanValue = BooleanValue.build(ref);
        Assert.assertTrue(booleanValue.isPresent());
        Assert.assertTrue(booleanValue.get().isTrue());
        Assert.assertEquals(ref.get(), ";rest");
    }
}
