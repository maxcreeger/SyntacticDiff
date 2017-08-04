package diff.complexity.expression;

import diff.complexity.SyntaxSizer;
import diff.complexity.expression.blocks.AbstractBlockSizer;
import diff.complexity.expression.statement.StatementSizer;
import parser.syntaxtree.expression.EmptyExpression;
import parser.syntaxtree.expression.Expression;
import parser.syntaxtree.expression.ExpressionVisitor;
import parser.syntaxtree.expression.VariableDeclaration;
import parser.syntaxtree.expression.blocks.AbstractBlock;
import parser.syntaxtree.expression.statement.Statement;

public final class ExpressionSizer extends SyntaxSizer<Expression> implements ExpressionVisitor<Integer> {
    public static final ExpressionSizer EXPRESSION_SIZER = new ExpressionSizer();

    @Override
    public int size(Expression obj) {
        return obj.acceptExpressionVisitor(this);
    }

    @Override
    public Integer visit(EmptyExpression emptyExpression) {
        return 1;
    }

    @Override
    public Integer visit(AbstractBlock block) {
        return AbstractBlockSizer.ABSTRACT_BLOCK_SIZER.size(block);
    }

    @Override
    public Integer visit(VariableDeclaration variable) {
        return VariableDeclarationSizer.VARIABLE_DECLARATION_SIZER.size(variable);
    }

    @Override
    public Integer visit(Statement statement) {
        return StatementSizer.STATEMENT_SIZER.size(statement);
    }

}
