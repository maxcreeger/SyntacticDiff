package diff.complexity.expression.statement;

import java.util.List;

import diff.complexity.SyntaxSizer;
import lexeme.java.tree.expression.statement.ChainedAccess;
import lexeme.java.tree.expression.statement.Statement;

public final class ChainedAccessSizer extends SyntaxSizer<ChainedAccess> {

	public static final ChainedAccessSizer CHAINED_ACCESS_SIZER = new ChainedAccessSizer();

	@Override
	public int size(ChainedAccess chainedAccess) {
		List<Statement> statements = chainedAccess.getStatements();
		return statements.size() + statements.stream().mapToInt(statement -> StatementSizer.STATEMENT_SIZER.size(statement)).sum();
	}
}
