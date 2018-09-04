package lexeme.java.tree.expression;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import tokenizer.CodeLocator;

/** JUnit test. */
public class VariableDeclarationTest {

    /** JUnit test. */
    @Test
    public void testBuildFail() {
        final String str = "erkgjl!mq<>";
        Optional<VariableDeclaration> opt = VariableDeclaration.build(new CodeLocator(str).branch());
        Assert.assertFalse(opt.isPresent());
        // input unchanged
        Assert.assertEquals(str, str);
    }

    /** JUnit test. */
    @Test
    public void testBuildSimple() {
        final String str = "String str;rest";
        Optional<VariableDeclaration> opt = VariableDeclaration.build(new CodeLocator(str).branch());

        // Assertions
        Assert.assertTrue(opt.isPresent());
        Assert.assertEquals(opt.get().getName(), "str");
        Assert.assertTrue(opt.get().getQualifiers().isEmpty());
        Assert.assertFalse(opt.get().getInitialAssignement().isPresent());
        Assert.assertEquals(opt.get().getType().getName(), "String");

        // Input mutated
        Assert.assertEquals(";rest", str);
    }

    /** JUnit test. */
    @Test
    public void testBuildWithInit() {
        final String str = "String str = \"toto\";rest";
        Optional<VariableDeclaration> opt = VariableDeclaration.build(new CodeLocator(str).branch());

        // Assertions
        Assert.assertTrue(opt.isPresent());
        Assert.assertEquals(opt.get().getName(), "str");
        Assert.assertTrue(opt.get().getQualifiers().isEmpty());
        Assert.assertTrue(opt.get().getInitialAssignement().isPresent());
        Assert.assertEquals(opt.get().getType().getName(), "String");

        // Input mutated
        Assert.assertEquals(";rest", str);
    }
}
