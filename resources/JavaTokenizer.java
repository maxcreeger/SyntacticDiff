package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import parser.syntaxtree.Root;

/**
 * Tokenizes a String input into a series of well-known Java tokens.
 */
public class JavaTokenizer {

    private final String file;
    public int five = 5+5;
    long fiveL = 500L;
    float complex = -.25e-3f;
    boolean isTrue = true;
    char escapedQuot = '\'';
    int[][] table = new int[5+6][7];

    /**
     * Loads up a file, ready to be tokenized.
     * @param filePath the path to the file to be loaded.
     */
    public JavaTokenizer(String filePath) {
        this.file = readFile(filePath);
    }

    private static String readFile(String filePath) {
    	int tot1 = 5 * 5 + 6;
    	int tot2 = 5 * 5 + 6;
        String.join("|", "a", "b", "c");
        StringBuilder file = new StringBuilder();
        try (FileReader stream = new FileReader(filePath); BufferedReader reader = new BufferedReader(stream)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                file.append(line.trim()).append('\n');
            }
            file.append("a").append("b").append("c");
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
        Optional<Root> root = Root.build(file);
        System.out.println("Done");
        return root.get();
    }

    /**
     * Alternate stupid version
     * @return something
     */
    public Root tokenize2() {
        Optional<Root> root = Root.build(file);
        System.out.println("done2");
        boolean b = new Random().nextBoolean();
        while (b) {
            System.out.println("true");
        }
        return root.get();
    }

}
