package diff.similarity.evaluator.expression.statement;

import diff.complexity.expression.statement.StatementSizer;
import diff.similarity.ExpressionSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
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

/**
 * Compares two {@link Statement}s.
 */
public class StatementSimilarityEvaluator extends SimilarityEvaluator<Statement>
        implements
            StatementVisitor<DualStatementComparator<? extends Statement>> {

    /** Instance */
    public static final StatementSimilarityEvaluator INSTANCE = new StatementSimilarityEvaluator();

    private StatementSimilarityEvaluator() {
        super(StatementSizer.STATEMENT_SIZER, "statement");
    }

    public Similarity eval(Statement stat1, Statement stat2) {
        if (stat1.getClass().equals(stat2.getClass())) {
            DualStatementComparator<? extends Statement> statementComparator = stat2.acceptStatementVisitor(this);
            return stat1.acceptStatementVisitor(statementComparator);
        } else {
            return new ExpressionSimilarity<>(0., StatementSizer.STATEMENT_SIZER.size(stat1, stat2), stat1, stat2);
        }
    }

    @Override
    public DualStatementComparator<VariableReference> visit(VariableReference variableReference) {
        return new DualStatementComparator<VariableReference>(variableReference);
    }

    @Override
    public DualStatementComparator<SelfReference> visit(SelfReference selfReference) {
        return new DualStatementComparator<SelfReference>(selfReference);
    }

    @Override
    public DualStatementComparator<Return> visit(Return return1) {
        return new DualStatementComparator<Return>(return1);
    }

    @Override
    public DualStatementComparator<NewInstance> visit(NewInstance newInstance) {
        return new DualStatementComparator<NewInstance>(newInstance);
    }

    @Override
    public DualStatementComparator<MethodInvocation> visit(MethodInvocation methodInvocation) {
        return new DualStatementComparator<MethodInvocation>(methodInvocation);
    }

    @Override
    public DualStatementComparator<ChainedAccess> visit(ChainedAccess chainedAccess) {
        return new DualStatementComparator<ChainedAccess>(chainedAccess);
    }

    @Override
    public DualStatementComparator<ArrayDeclaration> visit(ArrayDeclaration arrayDeclaration) {
        return new DualStatementComparator<ArrayDeclaration>(arrayDeclaration);
    }

    @Override
    public DualStatementComparator<PrimitiveValue> visit(PrimitiveValue primitiveValue) {
        return new DualStatementComparator<PrimitiveValue>(primitiveValue);
    }

    @Override
    public DualStatementComparator<ArrayAccess> visit(ArrayAccess arrayAccess) {
        return new DualStatementComparator<ArrayAccess>(arrayAccess);
    }

    @Override
    public DualStatementComparator<Operator> visit(Operator operator) {
        return new DualStatementComparator<Operator>(operator);
    }
}
