package lexeme.java.tree;

import diff.complexity.Showable;
import lexer.Structure;
import lexer.java.JavaLexer.JavaGrammar;

/**
 * Basic bloc of semantic. Could be anything that has a meaning in Java.
 */
public interface JavaSyntax extends Showable, Structure<JavaGrammar> {

    /**
     * Visitor pattern.
     * @param <T> the expected outcome of the visitor
     * @param visitor the visitor
     * @return the outcome of the visit
     */
    <T> T acceptSyntaxVisitor(JavaSyntaxVisitor<T> visitor);

}
