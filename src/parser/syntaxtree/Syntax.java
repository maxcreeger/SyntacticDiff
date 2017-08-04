package parser.syntaxtree;

import diff.complexity.Showable;

/**
 * Basic bloc of semantic. Could be anything that has a meaning in Java.
 */
public interface Syntax extends Showable {

    /**
     * Visitor pattern.
     * @param <T> the expected outcome of the visitor
     * @param visitor the visitor
     * @return the outcome of the visit
     */
    <T> T acceptSyntaxVisitor(SyntaxVisitor<T> visitor);

}
