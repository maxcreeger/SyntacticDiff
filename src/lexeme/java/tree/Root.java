package lexeme.java.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import diff.complexity.Showable;
import lexer.Structure;
import lexer.java.JavaLexer.JavaGrammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.CodeLocator.CodeBranch;
import tokenizer.CodeLocator.CodeLocation;

/**
 * A {@link Root} Object represents a whole Java file, with package, imports,
 * and one class definition.
 */
@Getter
@AllArgsConstructor
public class Root implements Showable, Structure<JavaGrammar> {

	private final PackageDeclaration packageDeclaration;
	private final List<ImportStatement> imports;
	private final ClassDeclaration classDeclaration;
	CodeLocation location;

	/**
	 * Attempts to build a {@link Root} object from a provided text.
	 * 
	 * @param wholeFile
	 *            a text
	 * @return optionally, a {@link Root} object
	 */
	public static Optional<Root> build(CodeBranch wholeFile) {
		CodeBranch input = wholeFile.fork();
		Optional<PackageDeclaration> optPackage = PackageDeclaration.build(input);
		if (!optPackage.isPresent()) {
			return Optional.empty();
		}
		List<ImportStatement> imports = new ArrayList<>();
		Optional<ImportStatement> optImport;
		do {
			optImport = ImportStatement.build(input);
			if (optImport.isPresent()) {
				imports.add(optImport.get());
			}
		} while (optImport.isPresent());

		Optional<ClassDeclaration> optClass = ClassDeclaration.build(input);
		if (!optClass.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(new Root(optPackage.get(), imports, optClass.get(), input.commit()));
	}

	@Override
	public String toString() {
		return String.join("\n", fullBreakdown(""));
	}

	@Override
	public List<String> fullBreakdown(String prefix) {
		final List<String> result = new ArrayList<>();
		result.add(prefix + "<RootFile>");
		final String rootPrefix = prefix + "r  ";
		result.addAll(packageDeclaration.fullBreakdown(rootPrefix));
		for (ImportStatement imp : imports) {
			result.addAll(imp.fullBreakdown(rootPrefix));
		}
		result.addAll(classDeclaration.fullBreakdown(rootPrefix));
		return result;
	}

	@Override
	public List<String> nativeFormat(String prefix) {
		final List<String> result = new ArrayList<>();
		result.add(prefix + "<RootFile>");
		final String rootPrefix = prefix + "r  ";
		result.addAll(packageDeclaration.nativeFormat(rootPrefix));
		for (ImportStatement imp : imports) {
			result.addAll(imp.nativeFormat(rootPrefix));
		}
		result.addAll(classDeclaration.nativeFormat(rootPrefix));
		return result;
	}
}
