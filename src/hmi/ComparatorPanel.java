package hmi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import diff.complexity.Showable;
import diff.similarity.CompositeSimilarity;
import diff.similarity.LeafSimilarity;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.NoSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.Similarity.SimilarityVisitor;
import diff.similarity.SimpleSimilarity;
import diff.similarity.SimpleSimilarity.ShowableString;
import diff.similarity.evaluator.RootSimilarityEvaluator;
import lexeme.java.JavaTokenizer;
import lexeme.java.tree.Root;

public class ComparatorPanel extends JPanel {

    private static final long serialVersionUID = -6578673034938473816L;

    String msg;
    Showable obj;
    Similarity similarity;

    FontMetrics metrics;
    int hgt;

    public ComparatorPanel(String message, Showable obj, Similarity similarity) {
        super();
        msg = message;
        this.obj = obj;
        this.similarity = similarity;
        setPreferredSize(new Dimension(1600, 6000));
        Font myFont = new Font("Consolas", Font.PLAIN, 12);
        setFont(myFont);
        // get the height of a line of text in this
        // font and render context
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (metrics == null) {
            // get metrics from the graphics
            metrics = getGraphics().getFontMetrics(getFont());
            hgt = metrics.getHeight();
        }
        if (msg != null) {
            new SimilarityPainter(g).drawShowable(new ShowableString(msg), 50, 50);
        } else if (obj != null) {
            new SimilarityPainter(g).drawShowable(obj, 50, 50);
        } else {
            drawSimilarity(similarity, g);
        }
    }

    public void drawSimilarity(Similarity similarity, Graphics g) {
        similarity.accept(new SimilarityPainter(g));
    }

    public void listAllFonts() {
        String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (int i = 0; i < fonts.length; i++) {
            System.out.println(fonts[i]);
        }
    }

    public static void main(String[] arg) throws FileNotFoundException, IOException {
        StringBuilder file = new StringBuilder();
        try (FileReader stream = new FileReader("resources/JavaTokenizer.java"); BufferedReader reader = new BufferedReader(stream)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                file.append(line).append('\n');
            }
        }
        Root root1 = new JavaTokenizer("resources/JavaTokenizer.java").tokenize();
        Root root2 = new JavaTokenizer("resources/JavaTokenizer2.java").tokenize();

        JFrame frame = new JFrame("flat file");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ComparatorPanel(file.toString(), null, null));
        frame.setVisible(true);
        frame.pack();

        frame = new JFrame("Root");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ComparatorPanel(null, root1, null));
        frame.setVisible(true);
        frame.pack();

        Similarity similarity = RootSimilarityEvaluator.INSTANCE.eval(root1, root2);

        frame = new JFrame("Similarity");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ComparatorPanel panel = new ComparatorPanel(null, null, similarity);
        JScrollPane scrollable = new JScrollPane(panel);
        frame.add(scrollable);
        frame.setVisible(true);
        frame.pack();
    }

    public class SimilarityPainter implements SimilarityVisitor<Void> {

        private static final int TAB = 50;
        Graphics g;
        int lineNum = 0;
        int indent = 0;

        public SimilarityPainter(Graphics g) {
            this.g = g;
        }

        @Override
        public Void visit(CompositeSimilarity similarity) {
            // g.drawLine(500, 50 + lineNum * hgt, 800, 50 + lineNum * hgt);
            List<Similarity> sub = similarity.subSimilarities();
            boxSimilarity(similarity);
            lineNum++; // TODO remove
            indent++;
            for (Similarity subSim : sub) {
                subSim.accept(this);
            }
            indent--;
            lineNum++;// TODO remove
            return null;
        }

        private void boxSimilarity(CompositeSimilarity similarity) {
            final List<String> showLeft = similarity.showLeft().show("");
            final List<String> showRight = similarity.showRight().show("");
            int leftWidth = showLeft.stream().mapToInt(metrics::stringWidth).max().orElse(1);
            int rightWidth = showRight.stream().mapToInt(metrics::stringWidth).max().orElse(1);
            int leftHeight = showLeft.size();
            int rightHeight = showRight.size();
            int maxHeight = Math.max(leftHeight, rightHeight) /* tTODO remove: */ + 1;
            g.setColor(new Color(255 - indent * 10, 255 - indent * 10, 255 - indent * 10));
            g.fillRect(50 + (indent) * TAB, 50 + lineNum * hgt, 800 + rightWidth, maxHeight * hgt);
            g.setColor(Color.black);
            g.drawRect(50 + (indent) * TAB, 50 + lineNum * hgt, 800 + rightWidth, maxHeight * hgt);
        }

        @Override
        public <S extends Showable> Void visit(LeafSimilarity<S> similarity) {
            S left = similarity.getObj1();
            S right = similarity.getObj2();
            drawSideBySide(left, right, lineNum, g);
            lineNum += Math.max(left.show("").size(), right.show("").size());
            return null;
        }

        @Override
        public <S extends Showable> Void visit(LeftLeafSimilarity<S> similarity) {
            List<S> left = similarity.getObj1();
            if (left.size() > 1) {
                indent++;
            }
            for (S subSim : left) {
                List<String> subShow = subSim.show("");
                drawShowable(subSim, 50 + TAB * indent, 50 + lineNum * hgt);
                lineNum += subShow.size();
            }
            if (left.size() > 1) {
                indent--;
            }
            return null;
        }

        @Override
        public <S extends Showable> Void visit(RightLeafSimilarity<S> similarity) {
            List<S> right = similarity.getObj2();
            if (right.size() > 1) {
                indent++;
            }
            for (S subSim : right) {
                List<String> subShow = subSim.show("");
                drawShowable(subSim, 800 + TAB * indent, 50 + lineNum * hgt);
                lineNum += subShow.size();
            }
            if (right.size() > 1) {
                indent--;
            }
            return null;
        }

        @Override
        public Void visit(SimpleSimilarity similarity) {
            ShowableString left = similarity.getLeftComment();
            ShowableString right = similarity.getRightComment();
            drawSideBySide(left, right, lineNum, g);
            lineNum += (Math.max(left.show("").size(), right.show("").size()));
            return null;
        }

        @Override
        public Void visit(NoSimilarity similarity) {
            // TODO Auto-generated method stub
            return null;
        }

        public void drawSideBySide(Showable left, Showable right, int startLine, Graphics g) {
            drawShowable(left, 50 + TAB * indent, 50 + startLine * hgt);
            drawShowable(right, 800 + TAB * indent, 50 + startLine * hgt);
        }

        public void drawShowable(Showable toDraw, int lateralOffset, int verticalOffset) {
            String[] lines = String.join("\n", toDraw.show("")).split("\n");
            int y = 0;
            Rectangle overall = null;
            for (String string : lines) {
                // get the advance of my text in this font and render context
                int adv = metrics.stringWidth(string);
                Rectangle newRect = new Rectangle(lateralOffset, verticalOffset + y, adv, hgt);
                overall = overall == null ? newRect : overall.union(newRect);
                g.drawRect(lateralOffset, verticalOffset + y, adv, hgt);
                g.drawString(string, lateralOffset, verticalOffset + hgt + y - 3);
                y += hgt;
            }
            if (overall != null) {
                g.drawRect(overall.x, overall.y, overall.width, overall.height);
            }
        }

    }

}
