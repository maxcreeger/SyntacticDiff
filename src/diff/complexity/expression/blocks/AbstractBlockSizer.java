package diff.complexity.expression.blocks;

import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.blocks.BlockVisitor;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
import diff.complexity.SyntaxSizer;
import diff.complexity.expression.ExpressionSizer;
import diff.complexity.expression.blocks.trycatchfinally.CatchBlockSizer;
import diff.complexity.expression.blocks.trycatchfinally.FinallyBlockSizer;
import diff.complexity.expression.blocks.trycatchfinally.TryBlockSizer;
import diff.complexity.expression.statement.StatementSizer;
import diff.similarity.evaluator.expression.blocks.PlaceholderBlock;

public final class AbstractBlockSizer extends SyntaxSizer<AbstractBlock> implements BlockVisitor<Integer> {
	public static final AbstractBlockSizer ABSTRACT_BLOCK_SIZER = new AbstractBlockSizer();

	@Override
	public int size(AbstractBlock obj) {
		return obj.acceptBlockVisitor(this);
	}

	@Override
	public Integer visit(TryCatchFinallyBlock tryCatchFinallyBlock) {
		int tryComplexity = TryBlockSizer.TRY_BLOCK_SIZER.size(tryCatchFinallyBlock.getTryBlock());
		int catchComplexity = CatchBlockSizer.CATCH_BLOCK_SIZER.size(tryCatchFinallyBlock.getCatchBlocks());
		int finallyComplexity = 0;
		if (tryCatchFinallyBlock.getFinallyBlock().isPresent()) {
			finallyComplexity = FinallyBlockSizer.FINALLY_BLOCK_SIZER.size(tryCatchFinallyBlock.getFinallyBlock().get());
		}
		return tryComplexity + catchComplexity + finallyComplexity;
	}

	@Override
	public Integer visit(WhileBlock whileBlock) {
		int evalComplexity = StatementSizer.STATEMENT_SIZER.size(whileBlock.getEvaluation());
		int bodyComplexity = ExpressionSizer.EXPRESSION_SIZER.size(whileBlock.getBody());
		return evalComplexity + bodyComplexity;
	}

	@Override
	public Integer visit(IfBlock ifBlock) {
		int evalComplexity = StatementSizer.STATEMENT_SIZER.size(ifBlock.getCondition());
		int thenComplexity = ExpressionSizer.EXPRESSION_SIZER.size(ifBlock.getThenExpressions());
		int elseComplexity = ExpressionSizer.EXPRESSION_SIZER.size(ifBlock.getElseExpressions());
		return evalComplexity + thenComplexity + elseComplexity;
	}

	@Override
	public Integer visit(ForBlock forBlock) {
		int initComplexity = ExpressionSizer.EXPRESSION_SIZER.size(forBlock.getInitialisation());
		int evalComplexity = StatementSizer.STATEMENT_SIZER.size(forBlock.getEvaluation());
		int iterComplexity = ExpressionSizer.EXPRESSION_SIZER.size(forBlock.getIteration());
		int bodyComplexity = ExpressionSizer.EXPRESSION_SIZER.size(forBlock.getBody());
		return initComplexity + evalComplexity + iterComplexity + bodyComplexity;
	}

	@Override
	public Integer visit(DoWhileBlock doWhileBlock) {
		int evalComplexity = StatementSizer.STATEMENT_SIZER.size(doWhileBlock.getEvaluation());
		int bodyComplexity = ExpressionSizer.EXPRESSION_SIZER.size(doWhileBlock.getBody());
		return evalComplexity + bodyComplexity;
	}

	@Override
	public Integer visit(PlaceholderBlock placeholderBlock) {
		return ExpressionSizer.EXPRESSION_SIZER.size(placeholderBlock.getBody());
	}

}
