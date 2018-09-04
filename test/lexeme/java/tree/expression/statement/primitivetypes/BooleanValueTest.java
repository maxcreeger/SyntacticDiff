package lexeme.java.tree.expression.statement.primitivetypes;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import tokenizer.CodeLocator;
import tokenizer.CodeLocator.CodeBranch;

public class BooleanValueTest {

    @Test
    public void testConstructorTrue() {
        CodeLocator locator = new CodeLocator("");
        BooleanValue val = new BooleanValue(false, locator.branch().commit());
        Assert.assertFalse(val.isTrue());
    }

    @Test
    public void testBuildFailed() {
        final String str = "treueue;rest";
        CodeLocator locator = new CodeLocator(str);
        Optional<BooleanValue> booleanValue = BooleanValue.build(locator.branch());
        Assert.assertFalse(booleanValue.isPresent());
    }

    @Test
    public void testBuildTrue() {
        final String str = "true;rest";
        CodeLocator locator = new CodeLocator(str);
        final CodeBranch branch = locator.branch();
        Optional<BooleanValue> booleanValue = BooleanValue.build(branch);
        Assert.assertTrue(booleanValue.isPresent());
        Assert.assertTrue(booleanValue.get().isTrue());
        Assert.assertEquals(branch.getRest(), ";rest");
    }
}
