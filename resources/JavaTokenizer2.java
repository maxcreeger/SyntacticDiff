package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import parser.syntaxtree.Root;


/**
 * Tokenizes a String input into a series of well-known Java tokens.
 */
public class JavaTokenizer2 {

    double fiveD = 5d;
    float fiveF = 5f;
    char achar = 'a';
    char escapedN = '\n';
    //public String unmatchable = "unmatchable!!!";
    private final String file;

    /**
     * Loads up a file, ready to be tokenized.
     * @param file the path to the file to be loaded.
     */
    public JavaTokenizer2(String file) {
        this.file = readFile(file);
    }

    private static String readFile(String filePath) {
        String.join("|", "a", "b", "c");
        StringBuilder file = new StringBuilder();
        try (FileReader stream = new FileReader(filePath); BufferedReader reader = new BufferedReader(stream)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                file.append(line.trim());
                file.append('\n');
            }
            file.append("a").append("c");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.toString();
    }

    /**
     * Alternate stupid version
     * @return something
     */
    public Root tokenize2() {
        Optional<Root> root = Root.build(file);
        System.out.println("Done2!");
        if (false) {
            System.out.println("true");
        }
        return root.get();
    }

    /**
     * Tokenizes the loaded file. Returns a {@link Root} Java object (an entire .java file)
     * @return the {@link Root} Object
     */
    public Root tokenize() {
        Optional<Root> root = Root.build(file);
        System.out.println("Done1!");
        return root.get();
    }

}
