package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lexeme.java.tree.ClassDeclaration;
import lexeme.java.tree.ClassName;
import lexeme.java.tree.ImportStatement;
import lexeme.java.tree.MethodDeclaration;
import lexeme.java.tree.ParameterTypeDeclaration;
import lexeme.java.tree.Root;
import lexeme.java.tree.JavaSyntax;
import lexeme.java.tree.expression.EmptyExpression;
import lexeme.java.tree.expression.Expression;
import lexeme.java.tree.expression.ExpressionVisitor;
import lexeme.java.tree.expression.VariableDeclaration;
import lexeme.java.tree.expression.blocks.AbstractBlock;
import lexeme.java.tree.expression.blocks.BlockVisitor;
import lexeme.java.tree.expression.blocks.DoWhileBlock;
import lexeme.java.tree.expression.blocks.ForBlock;
import lexeme.java.tree.expression.blocks.IfBlock;
import lexeme.java.tree.expression.blocks.WhileBlock;
import lexeme.java.tree.expression.blocks.trycatchfinally.TryCatchFinallyBlock;
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
import diff.similarity.evaluator.expression.blocks.PlaceholderBlock;

/**
 * Class to compute stuff on Java syntax.
 */
public class JavaLinkSolver {

	public static class Link {
		Object from;
		Object link;
		Object to;

		public Link(Object from, Object link, Object to) {
			this.from = from;
			this.link = link;
			this.to = to;
		}
	}

	public static Map<JavaSyntax, Scope> allScopes = new HashMap<>();

	public class Scope {

		private final class StatementRegisterer implements StatementVisitor<Void> {
			@Override
			public Void visit(VariableReference variableReference) {
				linkVariableReferenceToVariableDeclaration(variableReference);
				return null;
			}

			@Override
			public Void visit(SelfReference selfReference) {
				addSelfReference(selfReference);
				return null;
			}

			@Override
			public Void visit(Return return1) {
				return1.getReturnedValue().ifPresent(val -> register(val));
				return null;
			}

			@Override
			public Void visit(NewInstance newInstance) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visit(MethodInvocation methodInvocation) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visit(ChainedAccess chainedAccess) {
				for (Statement statement : chainedAccess.getStatements()) {
					statement.acceptStatementVisitor(this);
				}
				return null;
			}

			@Override
			public Void visit(ArrayDeclaration arrayDeclaration) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visit(PrimitiveValue primitiveValue) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visit(ArrayAccess arrayAccess) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visit(Operator operator) {
				// TODO Auto-generated method stub
				return null;
			}
		}

		private final class BlockRegisterer implements BlockVisitor<Void> {
			@Override
			public Void visit(TryCatchFinallyBlock tryCatchFinallyBlock) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visit(WhileBlock whileBlock) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visit(IfBlock ifBlock) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visit(ForBlock forBlock) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visit(DoWhileBlock doWhileBlock) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visit(PlaceholderBlock placeholderBlock) {
				// TODO Auto-generated method stub
				return null;
			}
		}

		private final class ExpressionRegisterer implements ExpressionVisitor<Void> {
			@Override
			public Void visit(EmptyExpression emptyExpression) {
				return null;
			}

			@Override
			public Void visit(AbstractBlock block) {
				block.acceptBlockVisitor(new BlockRegisterer());
				return null;
			}

			@Override
			public Void visit(VariableDeclaration variable) {
				Scope.this.variables.add(variable);
				variable.getInitialAssignement().ifPresent(assignment -> Scope.this.register(assignment));
				return null;
			}

			@Override
			public Void visit(Statement statement) {
				statement.acceptStatementVisitor(new StatementRegisterer());
				return null;
			}
		}

		final String name;
		final Scope superScope;
		final Object scopeObject;
		final ClassDeclaration currentClass;
		final List<String> imports = new ArrayList<>();
		final List<ParameterTypeDeclaration> parameterTypes = new ArrayList<>();
		final List<VariableDeclaration> variables = new ArrayList<>();
		final List<ClassDeclaration> classDeclarations = new ArrayList<>();
		final Map<ClassName, ClassDeclaration> classReference = new HashMap<>();
		final Map<VariableReference, VariableDeclaration> varReference = new HashMap<>();
		final Map<SelfReference, ClassDeclaration> selfReferences = new HashMap<>();

		public Scope(ClassDeclaration rootClass) {
			this.name = "root";
			this.currentClass = rootClass;
			this.scopeObject = rootClass;
			this.superScope = null;
			allScopes.put(rootClass, this);
		}

		private Scope(String name, Scope parent, ClassDeclaration currentClass, Object scopeObject) {
			this.name = name;
			this.superScope = parent;
			this.currentClass = currentClass;
			this.scopeObject = scopeObject;
			allScopes.put(currentClass, this);
		}

		private Scope(String name, Scope parent, ClassDeclaration currentClass) {
			this.name = name;
			this.superScope = parent;
			this.currentClass = currentClass;
			this.scopeObject = currentClass;
			allScopes.put(currentClass, this);
		}

		public Scope subscope(String name) {
			return new Scope(name, this, currentClass);
		}

		public Scope subscope(MethodDeclaration method) {
			return new Scope(name, this, this.currentClass, method);
		}

		public Scope subClass(String name, ClassDeclaration subClass) {
			Scope subclassScope = new Scope(name, this, subClass);
			subclassScope.register(subClass);
			classDeclarations.add(subClass);
			return subclassScope;
		}

		public void linkVariableReferenceToVariableDeclaration(VariableReference variableReference) {
			for (VariableDeclaration decl : variables) {
				if (decl.getName().equals(variableReference.getVariableName())) {
					varReference.put(variableReference, decl);
					return;
				}
			}
			if (superScope != null) {
				superScope.linkVariableReferenceToVariableDeclaration(variableReference);
			}
			throw new RuntimeException("Unable to link variableReference " + variableReference);
		}

		public void addSelfReference(SelfReference selfReference) {
			selfReferences.put(selfReference, currentClass);
		}

		private void register(ImportStatement importStatement) {
			for (String clazz : importStatement.getSuffixClasses()) {
				imports.add(clazz);
			}
		}

		private void register(VariableDeclaration variableDeclaration) {
			variables.add(variableDeclaration);
		}

		public List<Link> solve(Root root) {
			Scope rootScope = new Scope(root.getClassDeclaration());
			List<Link> links = new ArrayList<>();
			List<ImportStatement> imports = root.getImports();
			for (ImportStatement importStatement : imports) {
				rootScope.register(importStatement);
			}
			ClassDeclaration rootClass = root.getClassDeclaration();
			rootScope.register(rootClass); // register scope of declarations
			return links;
		}

		public void register(ClassDeclaration classDeclaration) {
			// Register static inner classes
			for (ClassDeclaration innerStatic : classDeclaration.getStaticInnerClasses()) {
				subClass("inner_static " + innerStatic.getClassName(), innerStatic);
			}
			// Register static fields
			for (VariableDeclaration decl : classDeclaration.getStaticFields()) {
				register(decl);
			}
			// Register static methods
			for (MethodDeclaration method : classDeclaration.getStaticMethods()) {
				register(method);
			}
			// Register inner classes
			Scope instanceScope = subscope("instance " + classDeclaration.getClassName().getName());
			for (ClassDeclaration innerStatic : classDeclaration.getInnerClasses()) {
				instanceScope.register(innerStatic);
			}
			// Register instance fields
			for (VariableDeclaration decl : classDeclaration.getFields()) {
				register(decl);
			}
			// Register methods
			for (MethodDeclaration method : classDeclaration.getMethods()) {
				instanceScope.register(method);
			}
		}

		private void register(MethodDeclaration method) {
			Scope methodScope = subscope("method  " + method.getName());
			for (ParameterTypeDeclaration parameter : method.getParameters()) {
				register(parameter);
			}
			for (Expression expression : method.getExpressions()) {
				register(expression);
			}
		}

		private void register(ParameterTypeDeclaration parameter) {
			Scope.this.parameterTypes.add(parameter);
		}

		private void register(Statement expression) {
			// TODO
		}

		private void register(Expression expression) {
			ExpressionVisitor<Void> expressionLinker = new ExpressionRegisterer();
			expression.acceptExpressionVisitor(expressionLinker);
		}
	}
}
