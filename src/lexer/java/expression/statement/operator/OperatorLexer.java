package lexer.java.expression.statement.operator;

import java.util.Optional;

import lexeme.java.tree.expression.statement.Statement;
import lexeme.java.tree.expression.statement.operators.Operator;
import lexeme.java.tree.expression.statement.operators.binary.Addition;
import lexeme.java.tree.expression.statement.operators.binary.Assignment;
import lexeme.java.tree.expression.statement.operators.binary.BooleanAnd;
import lexeme.java.tree.expression.statement.operators.binary.BooleanOr;
import lexeme.java.tree.expression.statement.operators.binary.Different;
import lexeme.java.tree.expression.statement.operators.binary.Division;
import lexeme.java.tree.expression.statement.operators.binary.Equals;
import lexeme.java.tree.expression.statement.operators.binary.Multiply;
import lexeme.java.tree.expression.statement.operators.binary.Subtraction;
import lexeme.java.tree.expression.statement.operators.unary.prefix.NotOperator;
import lexeme.java.tree.expression.statement.operators.unary.prefix.PreIncrementOperator;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lexer.java.JavaLexer.JavaSymbols;
import lexer.usual.structure.SingleSymbolStructureLexer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import tokenizer.TokenStream;
import tokenizer.tokens.Symbol;

/**
 * {@link StructureLexer} that creates a {@link NotOperator}.
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OperatorLexer<T extends Operator> implements StructureLexer<JavaGrammar, T> {

    public static OperatorLexer<Assignment> ASSIGNMENT_OPERATOR_LEXER =
            new OperatorLexer<Assignment>(false, JavaSymbols.EQUAL_EQUAL.lexer, true, (lhs, op, rhs) -> new Assignment(lhs, rhs));

    // Numeric
    public static OperatorLexer<PreIncrementOperator> PRE_INCREMENT_OPERATOR_LEXER =
            new OperatorLexer<PreIncrementOperator>(false, JavaSymbols.PLUSPLUS.lexer, true, (lhs, op, rhs) -> new PreIncrementOperator(rhs));
    public static OperatorLexer<Addition> ADDITION_OPERATOR_LEXER =
            new OperatorLexer<Addition>(false, JavaSymbols.PLUS.lexer, true, (lhs, op, rhs) -> new Addition(lhs, rhs));
    public static OperatorLexer<Subtraction> SUBTRACTION_OPERATOR_LEXER =
            new OperatorLexer<Subtraction>(false, JavaSymbols.MINUS.lexer, true, (lhs, op, rhs) -> new Subtraction(lhs, rhs));
    public static OperatorLexer<Multiply> MULTIPLICATION_OPERATOR_LEXER =
            new OperatorLexer<Multiply>(false, JavaSymbols.STAR.lexer, true, (lhs, op, rhs) -> new Multiply(lhs, rhs));
    public static OperatorLexer<Division> DIVISION_OPERATOR_LEXER =
            new OperatorLexer<Division>(false, JavaSymbols.DIVIDE.lexer, true, (lhs, op, rhs) -> new Division(lhs, rhs));

    // Booleans
    public static OperatorLexer<NotOperator> NOT_OPERATOR_LEXER =
            new OperatorLexer<NotOperator>(false, JavaSymbols.NOT.lexer, true, (lhs, op, rhs) -> new NotOperator(rhs));
    public static OperatorLexer<Different> DIFFERENT_OPERATOR_LEXER =
            new OperatorLexer<Different>(false, JavaSymbols.NOT.lexer, true, (lhs, op, rhs) -> new Different(lhs, rhs));
    public static OperatorLexer<Equals> EQUALITY_OPERATOR_LEXER =
            new OperatorLexer<Equals>(false, JavaSymbols.EQUAL.lexer, true, (lhs, op, rhs) -> new Equals(lhs, rhs));
    public static OperatorLexer<BooleanAnd> AND_OPERATOR_LEXER =
            new OperatorLexer<BooleanAnd>(false, JavaSymbols.AMPERSAND.lexer, true, (lhs, op, rhs) -> new BooleanAnd(lhs, rhs));
    public static OperatorLexer<BooleanOr> OR_OPERATOR_LEXER =
            new OperatorLexer<BooleanOr>(false, JavaSymbols.PIPE.lexer, true, (lhs, op, rhs) -> new BooleanOr(lhs, rhs));

    private final boolean hasLHS;
    private final SingleSymbolStructureLexer<JavaGrammar> symbolLexer;
    private final boolean hasRHS;
    private final Builder<T> builder;

    @FunctionalInterface
    interface Builder<T> {
        T build(Statement lhs, Symbol<JavaGrammar> symbol, Statement rhs);
    }

    @Override
    public Optional<? extends T> lex(TokenStream<JavaGrammar> input) {
        TokenStream<JavaGrammar> fork = input.fork();
        final Statement lhs;
        if (hasLHS) {
            Optional<? extends Statement> lhsOpt = Statement.STATEMENT_LEXER.lex(fork);
            if (lhsOpt.isPresent()) {
                lhs = lhsOpt.get();
            } else {
                return Optional.empty();
            }
        } else {
            lhs = null;
        }
        Optional<Symbol<JavaGrammar>> operator = symbolLexer.lex(fork);
        if (!operator.isPresent()) {
            return Optional.empty();
        }
        final Statement rhs;
        if (hasRHS) {
            Optional<? extends Statement> rhsOpt = Statement.STATEMENT_LEXER.lex(fork);
            if (rhsOpt.isPresent()) {
                rhs = rhsOpt.get();
            } else {
                return Optional.empty();
            }
        } else {
            rhs = null;
        }
        return Optional.of(builder.build(lhs, operator.get(), rhs));
    }

}
