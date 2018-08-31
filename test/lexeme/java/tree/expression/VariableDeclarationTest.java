package lexeme.java.tree.expression;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

import lexeme.java.tree.expression.VariableDeclaration;

/** JUnit test. */
public class VariableDeclarationTest {

    /** JUnit test. */
    @Test
    public void testBuildFail() {
        final String str = "erkgjl!mq<>";
        final AtomicReference<String> inputRef = new AtomicReference<String>(str);
        Optional<VariableDeclaration> opt = VariableDeclaration.build(inputRef);
        Assert.assertFalse(opt.isPresent());
        // input unchanged
        Assert.assertEquals(inputRef.get(), str);
    }

    /** JUnit test. */
    @Test
    public void testBuildSimple() {
        final AtomicReference<String> inputRef = new AtomicReference<String>("String str;rest");
        Optional<VariableDeclaration> opt = VariableDeclaration.build(inputRef);

        // Assertions
        Assert.assertTrue(opt.isPresent());
        Assert.assertEquals(opt.get().getName(), "str");
        Assert.assertTrue(opt.get().getQualifiers().isEmpty());
        Assert.assertFalse(opt.get().getInitialAssignement().isPresent());
        Assert.assertEquals(opt.get().getType().getName(), "String");

        // Input mutated
        Assert.assertEquals(";rest", inputRef.get());
    }

    /** JUnit test. */
    @Test
    public void testBuildWithInit() {
        final AtomicReference<String> inputRef = new AtomicReference<String>("String str = \"toto\";rest");
        Optional<VariableDeclaration> opt = VariableDeclaration.build(inputRef);

        // Assertions
        Assert.assertTrue(opt.isPresent());
        Assert.assertEquals(opt.get().getName(), "str");
        Assert.assertTrue(opt.get().getQualifiers().isEmpty());
        Assert.assertTrue(opt.get().getInitialAssignement().isPresent());
        Assert.assertEquals(opt.get().getType().getName(), "String");

        // Input mutated
        Assert.assertEquals(";rest", inputRef.get());
    }
}
