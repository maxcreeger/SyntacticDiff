package lexer.java;

import static lexer.java.JavaLexer.JavaSymbols.DOT;
import static lexer.java.JavaLexer.JavaSymbols.PACKAGE;
import static lexer.java.JavaLexer.JavaSymbols.SEMICOLON;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import lexeme.java.tree.PackageDeclaration;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lexer.usual.WhitespaceDetector;
import lexer.usual.WordDetector;
import tokenizer.Token;
import tokenizer.TokenStream;
import tokenizer.java.JavaCodeTokenizer;

/** Detects a Java {@link PackageDeclaration}. */
public class PackageDeclarationDetector implements StructureLexer<JavaGrammar, PackageDeclaration> {

	private static final WhitespaceDetector WHITESPACE_DETECTOR = new WhitespaceDetector();
	private static final WordDetector PACKAGE_NAME_DETECTOR = new WordDetector(token -> token.get().matches("[a-z][a-z0-9]*"));

	@Override
	public Optional<PackageDeclaration> lex(TokenStream<JavaGrammar> input) {
		TokenStream<JavaGrammar> fork = input.fork();
		if (!PACKAGE.detector.visitAny(fork.next())) {
			return Optional.empty();
		}
		if (!WHITESPACE_DETECTOR.visitAny(fork.next())) {
			return Optional.empty();
		}
		List<String> packagePath = new ArrayList<>();
		while (true) {
			Token<JavaGrammar> name = fork.next();
			if (name.accept(PACKAGE_NAME_DETECTOR)) {
				packagePath.add(name.get());
			} else {
				return Optional.empty();
			}
			final Token<JavaGrammar> next = fork.next();
			if (next.accept(DOT.detector)) {
				continue;
			} else if (next.accept(SEMICOLON.detector)) {
				fork.commit();
				return Optional.of(new PackageDeclaration(packagePath));
			} else {
				return Optional.empty();
			}
		}
	}

	/**
	 * Test.
	 * 
	 * @param argc
	 *            unused
	 */
	public static void main(String[] argc) {
		// Tokenize
		String code = "package com.stuff.lexer;";
		Optional<List<Token<JavaGrammar>>> tokenList = JavaCodeTokenizer.TOKENIZER.tokenizeAll(new AtomicReference<String>(code));

		// Give structure
		TokenStream<JavaGrammar> tokenStream = TokenStream.of(tokenList.get());
		Optional<PackageDeclaration> packageDeclaration = new PackageDeclarationDetector().lex(tokenStream);
		packageDeclaration.get();
	}
}
