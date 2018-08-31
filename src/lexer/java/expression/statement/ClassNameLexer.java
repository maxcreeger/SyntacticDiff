package lexer.java.expression.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import lexeme.java.tree.ClassName;
import lexer.StructureLexer;
import lexer.java.JavaLexer.JavaGrammar;
import lexer.java.JavaLexer.JavaSymbols;
import lexer.usual.structure.SingleWordStructureLexer;
import tokenizer.TokenStream;
import tokenizer.tokens.Word;

public class ClassNameLexer implements StructureLexer<JavaGrammar, ClassName> {

    private static final Pattern classNamePattern = Pattern.compile("\\w+");
    private static final SingleWordStructureLexer<JavaGrammar> SIMPLE_CLASS_NAME_LEXER =
            new SingleWordStructureLexer<>(word -> classNamePattern.matcher(word.getWord()).matches());

    @Override
    public Optional<ClassName> lex(TokenStream<JavaGrammar> input) {
        TokenStream<JavaGrammar> fork = input.fork();
        Optional<Word<JavaGrammar>> className = SIMPLE_CLASS_NAME_LEXER.lex(input);
        if (!className.isPresent()) {
            // Not a class name!
            return Optional.empty();
        }
        // Find generics, may be nothing in the chevrons ("<>")
        List<ClassName> found = null;
        if (JavaSymbols.OPEN_CHEVRONS.lexer.lex(fork).isPresent()) {
            found = new ArrayList<>(); // Non null!
            if (findGenericParameter(fork, found)) {
                while (findSeparator(fork)) {
                    // Wait until no separator generic is found
                    if (!findGenericParameter(fork, found)) {
                        return Optional.empty(); // Found separator, but no type after it!
                    }
                }
            } else {
                // Empty chevrons, maybe?
            }
            if (!JavaSymbols.CLOSE_CHEVRONS.lexer.lex(fork).isPresent()) {
                return Optional.empty(); // No closing chevrons!
            }
        }

        // Find super class
        final Optional<ClassName> superClass;
        final TokenStream<JavaGrammar> forkSuper = fork.fork();
        if (JavaSymbols.SUPER.lexer.lex(forkSuper).isPresent()) {
            Optional<ClassName> theSuperName = this.lex(forkSuper);
            if (theSuperName.isPresent()) {
                forkSuper.commit();
                superClass = theSuperName;
            } else {
                return Optional.empty(); // Invalid class name after 'super' keyword
            }
        } else {
            superClass = Optional.empty();
        }

        // Find extends class
        final Optional<ClassName> extendsClass;
        final TokenStream<JavaGrammar> forkExtends = fork.fork();
        if (JavaSymbols.EXTENDS.lexer.lex(forkExtends).isPresent()) {
            Optional<ClassName> theExtendsName = this.lex(forkExtends);
            if (theExtendsName.isPresent()) {
                forkExtends.commit();
                extendsClass = theExtendsName;
            } else {
                return Optional.empty(); // Invalid class name after 'extends' keyword
            }
        } else {
            extendsClass = Optional.empty();
        }

        // find interfaces
        final List<ClassName> interfaceClasses = new ArrayList<>();
        TokenStream<JavaGrammar> forkInterfaces = fork.fork();
        if (JavaSymbols.IMPLEMENTS.lexer.lex(forkInterfaces).isPresent()) {
            Optional<ClassName> firstInterface = this.lex(forkInterfaces);
            if (firstInterface.isPresent()) {
                while (JavaSymbols.COMA.lexer.lex(forkInterfaces).isPresent()) {
                    Optional<ClassName> theInterfaceName = this.lex(forkSuper);
                    if (theInterfaceName.isPresent()) {
                        interfaceClasses.add(theInterfaceName.get());
                    } else {
                        return Optional.empty(); // No interface after interface separator!
                    }
                }
            } else {
                return Optional.empty(); // No interface after 'interface' keyword!
            }
        }

        // Count dimensions
        int openBrackets = 0;
        while (JavaSymbols.OPEN_BRACKETS.lexer.lex(fork).isPresent()) {
            openBrackets++;
        }
        int closeBrackets = 0;
        while (closeBrackets < openBrackets && JavaSymbols.CLOSE_BRACKETS.lexer.lex(fork).isPresent()) {
            closeBrackets++;
        }
        if (openBrackets != closeBrackets) {
            return Optional.empty(); // Unbalanced brackets
        }

        return Optional.of(new ClassName(className.get().getWord(), found, openBrackets, superClass, extendsClass, interfaceClasses));
    }

    private boolean findSeparator(TokenStream<JavaGrammar> fork) {
        return JavaSymbols.COMA.lexer.lex(fork).isPresent();
    }

    private boolean findGenericParameter(TokenStream<JavaGrammar> fork, List<ClassName> found) {
        Optional<ClassName> internalClassName = this.lex(fork);
        if (internalClassName.isPresent()) {
            found.add(internalClassName.get());
            return true;
        }
        return false;
    }

}
