package lexeme.java.tree.expression.statement.operators;

import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.StatementVisitor;
import tokenizer.CodeLocator.CodeLocation;

/**
 * Represents an Operator. could be Unary (!true) or binary (a + b).
 */
public abstract class Operator extends Statement {

	public Operator(CodeLocation location) {
		super(location);
	}

	@Override
	public boolean isAssignable() {
		return false;
	}

	/**
	 * A enumeration of known operators
	 */
	public static enum OperatorsEnum {
		/** Not Unary operator (!true) */
		NOT,
		/** Pre-increment Unary operator (++n) */
		PRE_INCREMENT,
		/** Boolean And Binary operator (true & false) */
		AND,
		/** Boolean Or Binary operator (true | false) */
		OR,
		/** Addition Binary operator (a + b) */
		ADD,
		/** Subtraction Binary operator (a - b) */
		SUBSTRACT,
		/** Multiplication Binary operator (a * b) */
		MULTIPLY,
		/** Division Binary operator (a / b) */
		DIVIDE,
		/** Assignment Binary operator (a = 5) */
		ASSIGN,
		/** Boolean equality Binary operator (a == b) */
		EQUALS
	}

	@Override
	public <T> T acceptStatementVisitor(StatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Accept to be visited by an {@link OperatorVisitor}.
	 * 
	 * @param <T>
	 *            the returned object type
	 * @param visitor
	 *            the visitor
	 * @return the returned object
	 */
	public abstract <T> T acceptOperatorVisitor(OperatorVisitor<T> visitor);

	/**
	 * Returns the {@link String} representation of this operator.
	 * 
	 * @return a String representation
	 */
	public abstract String getOperatorSymbol();
}
