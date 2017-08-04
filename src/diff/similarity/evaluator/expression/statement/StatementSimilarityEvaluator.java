package diff.similarity.evaluator.expression.statement;

import diff.complexity.expression.statement.StatementSizer;
import diff.similarity.ExpressionSimilarity;
import diff.similarity.Similarity;
import diff.similarity.evaluator.SimilarityEvaluator;
import parser.syntaxtree.expression.statement.ArrayAccess;
import parser.syntaxtree.expression.statement.ArrayDeclaration;
import parser.syntaxtree.expression.statement.ChainedAccess;
import parser.syntaxtree.expression.statement.MethodInvocation;
import parser.syntaxtree.expression.statement.NewInstance;
import parser.syntaxtree.expression.statement.Return;
import parser.syntaxtree.expression.statement.SelfReference;
import parser.syntaxtree.expression.statement.Statement;
import parser.syntaxtree.expression.statement.StatementVisitor;
import parser.syntaxtree.expression.statement.VariableReference;
import parser.syntaxtree.expression.statement.operators.Operator;
import parser.syntaxtree.expression.statement.primitivetypes.PrimitiveValue;

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
