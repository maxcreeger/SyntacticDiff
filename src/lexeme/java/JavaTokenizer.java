package lexeme.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import lexeme.java.tree.Root;
import tokenizer.CodeLocator;

/**
 * Tokenizes a String input into a series of well-known Java tokens.
 */
public class JavaTokenizer {

    private final String file;

    /**
     * Loads up a file, ready to be tokenized.
     * @param filePath the path to the file to be loaded.
     */
    public JavaTokenizer(String filePath) {
        this.file = readFile(filePath);
    }

    private static String readFile(String filePath) {
        StringBuilder file = new StringBuilder();
        try (FileReader stream = new FileReader(filePath); BufferedReader reader = new BufferedReader(stream)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                file.append(line.trim()).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.toString();
    }

    /**
     * Tokenizes the loaded file. Returns a {@link Root} Java object (an entire .java file)
     * @return the {@link Root} Object
     */
    public Root tokenize() {
        Optional<Root> root = Root.build(new CodeLocator(file).branch());
        return root.get();
    }

}
