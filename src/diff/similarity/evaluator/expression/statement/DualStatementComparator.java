package diff.similarity.evaluator.expression.statement;

import diff.similarity.ExpressionSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.ClassNameSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.operator.OperatorSimilarityEvaluator;
import diff.similarity.evaluator.expression.statement.primitivetypes.PrimitiveValueSimilarityEvaluator;
import lexeme.java.tree.expression.statement.ArrayAccess;
import lexeme.java.tree.expression.statement.ArrayDeclaration;
import lexeme.java.tree.expression.statement.ChainedAccess;
import lexeme.java.tree.expression.statement.MethodInvocation;
import lexeme.java.tree.expression.statement.NewInstance;
import lexeme.java.tree.expression.statement.Return;
import lexeme.java.tree.expression.statement.SelfReference;
import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.StatementVisitor;
import lexeme.java.tree.expression.statement.VariableReference;
import lexeme.java.tree.expression.statement.operators.Operator;
import lexeme.java.tree.expression.statement.primitivetypes.PrimitiveValue;
import lombok.AllArgsConstructor;

/**
 * Compares two {@link Statement}s that must have the same type.
 *
 * @param <T> the common type of statements
 */
@AllArgsConstructor
public class DualStatementComparator<T extends Statement> implements StatementVisitor<Similarity> {

    private final T expr2;

    @Override
    public Similarity visit(VariableReference variableReference) {
        return VariableReferenceSimilarityEvaluator.INSTANCE.eval(variableReference, (VariableReference) expr2);
    }

    @Override
    public Similarity visit(SelfReference selfReference) {
        return new ExpressionSimilarity<>(1, 1, selfReference, (SelfReference) expr2);
    }

    @Override
    public Similarity visit(Return return1) {
        Return return2 = (Return) expr2;
        return ReturnSimilarityEvaluator.INSTANCE.eval(return1, return2);
    }

    @Override
    public Similarity visit(NewInstance newInstance1) {
        NewInstance newInstance2 = (NewInstance) expr2;
        return NewInstanceSimilarityEvaluator.INSTANCE.eval(newInstance1, newInstance2);
    }

    @Override
    public Similarity visit(MethodInvocation methodInvocation1) {
        MethodInvocation methodInvocation2 = (MethodInvocation) expr2;
        return MethodInvocationSimilarityEvaluator.INSTANCE.eval(methodInvocation1, methodInvocation2);
    }

    @Override
    public Similarity visit(ChainedAccess chainedAccess1) {
        // Normal compare: source/source and action/action
        ChainedAccess chainedAccess2 = (ChainedAccess) expr2;
        Similarity subStatementSim =
                StatementSimilarityEvaluator.INSTANCE.orderedEval(chainedAccess1.getStatements(), chainedAccess2.getStatements());
        return subStatementSim;
    }

    @Override
    public Similarity visit(ArrayDeclaration arrayDeclaration1) {
        ArrayDeclaration arrayDeclaration2 = (ArrayDeclaration) expr2;
        Similarity classSim = ClassNameSimilarityEvaluator.INSTANCE.eval(arrayDeclaration1.getClassName(), arrayDeclaration2.getClassName());
        Similarity initSim = ArrayInitializationSimilarityEvaluator.INSTANCE.eval(arrayDeclaration1.getInit(), arrayDeclaration2.getInit());
        return Similarity.add("array-declare", classSim, initSim);
    }

    @Override
    public Similarity visit(PrimitiveValue primitiveValue1) {
        return PrimitiveValueSimilarityEvaluator.INSTANCE.eval(primitiveValue1, (PrimitiveValue) expr2);
    }

    @Override
    public Similarity visit(ArrayAccess arrayAccess1) {
        ArrayAccess arrayAccess2 = (ArrayAccess) expr2;
        Similarity sourceSim = StatementSimilarityEvaluator.INSTANCE.eval(arrayAccess1.getSource(), arrayAccess2.getSource());
        Similarity indexSim = StatementSimilarityEvaluator.INSTANCE.eval(arrayAccess1.getIndex(), arrayAccess2.getIndex());
        return Similarity.add("array-access", sourceSim, indexSim);
    }

    @Override
    public Similarity visit(Operator operator1) {
        return OperatorSimilarityEvaluator.INSTANCE.eval(operator1, (Operator) expr2);
    }

}
