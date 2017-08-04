package parser.syntaxtree;

import parser.syntaxtree.expression.Expression;

/**
 * Visitor of a {@link Syntax} object.
 *
 * @param <T> product of the visitor
 */
public interface SyntaxVisitor<T> {
    /**
     * visit a {@link Root} object.
     * @param root the visited object
     * @return the outcome of the visit
     */
    T visit(Root root);

    /**
     * visit a {@link PackageDeclaration} object.
     * @param packageDeclaration the visited object
     * @return the outcome of the visit
     */
    T visit(PackageDeclaration packageDeclaration);


    /**
     * visit a {@link ImportStatement} object.
     * @param importStatement the visited object
     * @return the outcome of the visit
     */
    T visit(ImportStatement importStatement);

    /**
     * visit a {@link ClassDeclaration} object.
     * @param classDec the visited object
     * @return the outcome of the visit
     */
    T visit(ClassDeclaration classDec);

    /**
     * visit a {@link ClassName} object.
     * @param className the visited object
     * @return the outcome of the visit
     */
    T visit(ClassName className);

    /**
     * visit a {@link MethodDeclaration} object.
     * @param methodDeclaration the visited object
     * @return the outcome of the visit
     */
    T visit(MethodDeclaration methodDeclaration);

    /**
     * visit a {@link ParameterPassing} object.
     * @param param the visited object
     * @return the outcome of the visit
     */
    T visit(ParameterPassing param);

    /**
     * visit a {@link Qualifiers} object.
     * @param qualifier the visited object
     * @return the outcome of the visit
     */
    T visit(Qualifiers qualifier);

    /**
     * visit a {@link Expression} object.
     * @param expression the visited object
     * @return the outcome of the visit
     */
    T visit(Expression expression);

}
