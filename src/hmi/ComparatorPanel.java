package hmi;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import diff.complexity.Showable;
import diff.similarity.CompositeSimilarity;
import diff.similarity.CompositeSimilarity.CompositeSimilarityImpl;
import diff.similarity.LeafSimilarity;
import diff.similarity.LeftLeafSimilarity;
import diff.similarity.NoSimilarity;
import diff.similarity.RightLeafSimilarity;
import diff.similarity.Similarity;
import diff.similarity.Similarity.SimilarityVisitor;
import diff.similarity.SimpleSimilarity;
import diff.similarity.SimpleSimilarity.ShowableString;
import diff.similarity.evaluator.ClassDeclarationSimilarityEvaluator.ClassDeclarationSimilarity;
import diff.similarity.evaluator.ClassNameSimilarityEvaluator.ClassNameSimilarity;
import diff.similarity.evaluator.MethodDeclarationSimilarityEvaluator.MethodDeclarationSimilarity;
import diff.similarity.evaluator.RootSimilarityEvaluator;
import diff.similarity.evaluator.RootSimilarityEvaluator.RootSimilarity;
import diff.similarity.evaluator.expression.VariableDeclarationSimilarityEvaluator.VariableDeclarationSimilarity;
import diff.similarity.evaluator.expression.statement.ChainedAccessSimilarityEvaluator.ChainedAccessSimilarity;
import diff.similarity.evaluator.expression.statement.MethodInvocationSimilarityEvaluator.MethodInvocationSimilarity;
import lexeme.java.JavaTokenizer;
import lexeme.java.tree.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class ComparatorPanel extends JPanel {

    private static final long serialVersionUID = -6578673034938473816L;
    private static final int TAB = 30;

    private static Random rand = new Random();

    static Font myFont = new Font("Consolas", Font.PLAIN, 12);
    static Font rotatedFont;
    static {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(-Math.PI / 2, 0, 0);
        rotatedFont = myFont.deriveFont(affineTransform);
    }
    static final Canvas canvas = new Canvas();
    static final FontMetrics metrics = canvas.getFontMetrics(myFont);
    static final int hgt = metrics.getHeight();;
    DrawableCode drawableString;
    DrawableCode drawableShowableNative;
    DrawableStructure drawableSimilarity;

    public ComparatorPanel(String message) {
        super();
        setPreferredSize(new Dimension(1600, 2000));
        setFont(myFont);
        drawableString = new SimilarityPainter().drawNativeShowable(new ShowableString(message, null), Color.white);
    }

    public ComparatorPanel(Showable obj, boolean isNative) {
        super();
        setPreferredSize(new Dimension(1600, isNative ? 2000 : 3000));
        setFont(myFont);

        if (isNative) {
            drawableShowableNative = new SimilarityPainter().drawNativeShowable(obj, Color.white);
        } else {
            drawableShowableNative = new SimilarityPainter().drawExpandedShowable(obj, Color.white);
        }
    }

    public ComparatorPanel(Similarity similarity) {
        super();
        setPreferredSize(new Dimension(1600, 5000));
        setFont(myFont);

        drawableSimilarity = drawSimilarity(similarity);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        if (drawableString != null) {
            drawableString.draw(new Point(50, 50), g2d);
        }
        if (drawableShowableNative != null) {
            drawableShowableNative.draw(new Point(50, 50), g2d);
        }
        if (drawableSimilarity != null) {
            drawableSimilarity.draw(g2d);
        }
    }

    public DrawableStructure drawSimilarity(Similarity similarity) {
        return similarity.accept(new SimilarityPainter());
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
        frame.add(new JScrollPane(new ComparatorPanel(file.toString())));
        frame.setVisible(true);
        frame.pack();

        frame = new JFrame("Expanded");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(new ComparatorPanel(root1, false)));
        frame.setVisible(true);
        frame.pack();

        frame = new JFrame("Native");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(new ComparatorPanel(root1, true)));
        frame.setVisible(true);
        frame.pack();

        Similarity similarity = RootSimilarityEvaluator.INSTANCE.eval(root1, root2);

        frame = new JFrame("Similarity");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(new ComparatorPanel(similarity)));
        frame.setVisible(true);
        frame.pack();
    }

    public static class SimilarityPainter implements SimilarityVisitor<DrawableStructure> {


        @Override
        public DrawableStructure visit(RootSimilarity similarity) {
            return visit((CompositeSimilarity) similarity); // TODO dedicated implementation
        }

        @Override
        public DrawableStructure visit(ClassDeclarationSimilarity similarity) {
            return visit((CompositeSimilarity) similarity); // TODO dedicated implementation
        }

        @Override
        public DrawableStructure visit(ChainedAccessSimilarity similarity) {
            return visit((CompositeSimilarity) similarity); // TODO dedicated implementation
        }

        @Override
        public DrawableStructure visit(ClassNameSimilarity similarity) {
            return visit((CompositeSimilarity) similarity); // TODO dedicated implementation
        }

        @Override
        public DrawableStructure visit(VariableDeclarationSimilarity similarity) {
            return visit((CompositeSimilarity) similarity); // TODO dedicated implementation
        }

        @Override
        public DrawableStructure visit(CompositeSimilarityImpl similarity) {
            return visit((CompositeSimilarity) similarity); // TODO dedicated implementation
        }

        @Override
        public DrawableStructure visit(MethodInvocationSimilarity similarity) {
            return visit((CompositeSimilarity) similarity); // TODO dedicated implementation
        }

        @Override
        public DrawableStructure visit(MethodDeclarationSimilarity similarity) {
            if (similarity.similarity() < 0.9999999) {
                List<Similarity> sub = similarity.subSimilarities();
                DrawableTree total = new DrawableTree(similarity.getName(), Color.BLACK);
                for (Similarity sim : sub) {
                    DrawableStructure drawable = sim.accept(this);
                    if (drawable != null) {
                        total.addContent(drawable);
                    }
                }
                return total;
            } else {
                Color color = Color.LIGHT_GRAY;
                DrawableCode leftRect = drawNativeShowable(similarity.showLeft(), color);
                DrawableCode rightRect = drawNativeShowable(similarity.showRight(), color);
                return new DrawableSideBySide(similarity.getName(), color, leftRect, rightRect);
            }
        }

        private DrawableStructure visit(CompositeSimilarity similarity) {
            if (similarity.similarity() < 0.9999999) {
                List<Similarity> sub = similarity.subSimilarities();
                DrawableTree total = new DrawableTree(similarity.getName(), Color.BLACK);
                for (Similarity sim : sub) {
                    DrawableStructure drawable = sim.accept(this);
                    if (drawable != null) {
                        total.addContent(drawable);
                    }
                }
                return total;
            } else {
                Color color = Color.LIGHT_GRAY;
                DrawableCode leftRect = drawNativeShowable(similarity.showLeft(), color);
                DrawableCode rightRect = drawNativeShowable(similarity.showRight(), color);
                return new DrawableSideBySide(similarity.getName(), color, leftRect, rightRect);
            }
        }

        @Override
        public <S extends Showable> DrawableSideBySide visit(LeafSimilarity<S> similarity) {
            S left = similarity.getObj1();
            S right = similarity.getObj2();
            Color color;
            DrawableCode leftRect;
            DrawableCode rightRect;
            if (similarity.similarity() < 0.9999999) {
                color = Color.CYAN;
                leftRect = drawExpandedShowable(left, color);
                rightRect = drawExpandedShowable(right, color);
            } else {
                color = Color.LIGHT_GRAY;
                leftRect = drawNativeShowable(left, color);
                rightRect = drawNativeShowable(right, color);
            }
            return new DrawableSideBySide(similarity.getName(), color, leftRect, rightRect);
        }

        @Override
        public <S extends Showable> DrawableStructure visit(LeftLeafSimilarity<S> similarity) {
            S leftObject = similarity.getObj1();
            DrawableCode left = drawNativeShowable(leftObject, Color.GREEN);
            return new DrawableSideBySide("left", Color.GREEN, left, new DrawableRectangle(left.getWidth(), left.getHeight(), Color.white, Color.GREEN));
        }

        @Override
        public <S extends Showable> DrawableStructure visit(RightLeafSimilarity<S> similarity) {
            S rightObject = similarity.getObj2();
            DrawableCode right = drawNativeShowable(rightObject, Color.PINK);
            return new DrawableSideBySide("right", Color.PINK, new DrawableRectangle(right.getWidth(), right.getHeight(), Color.white, Color.PINK), right);
        }

        @Override
        public DrawableSideBySide visit(SimpleSimilarity similarity) {
            ShowableString leftShowable = similarity.getLeftComment();
            ShowableString rightShowable = similarity.getRightComment();
            Color color = similarity.similarity() < 0.9999999 ? Color.CYAN : Color.LIGHT_GRAY;
            DrawableCode leftDrawable = drawNativeShowable(leftShowable, color);
            DrawableCode rightDrawable = drawNativeShowable(rightShowable, color);
            return new DrawableSideBySide("???!!!?", color, leftDrawable, rightDrawable);
        }

        @Override
        public DrawableStructure visit(NoSimilarity similarity) {
            // TODO Auto-generated method stub
            return new DrawableSideBySide("NOTHINNGGGG", Color.orange, new DrawableRectangle(0, 0, Color.YELLOW, Color.orange),
                    new DrawableRectangle(0, 0, Color.YELLOW, Color.orange));
        }

        public DrawableCode drawExpandedShowable(Showable toDraw, Color color) {
            List<String> lines = toDraw.fullBreakdown("");
            List<DrawableCode> drawables = new ArrayList<>();
            for (String string : lines) {
                drawables.add(new DrawableString(string, color));
            }
            if (drawables.size() == 1) {
                return drawables.get(0);
            } else {
                return new DrawableCodeList("showables??", color, drawables.toArray(new DrawableCode[drawables.size()])); // TODO fix name
            }
        }

        public DrawableCode drawNativeShowable(Showable toDraw, Color color) {
            List<String> lines = toDraw.nativeFormat("");
            return new DrawableString(String.join("\n", lines), color);
        }
    }

        public static abstract class DrawableStructure {

            String name;
            Color color;
            @Getter
            int textHeight;

            @Setter
            DrawableStructure parentDrawable;

            @Setter
            DrawableStructure aboveDrawable;

            public DrawableStructure(String name, Color color) {
                this.name = name;
                this.color = color;
                this.textHeight = metrics.stringWidth(name);
            }

            public int getLeft() {
                if (aboveDrawable != null) {
                    return aboveDrawable.getLeft();
                } else if (parentDrawable != null) {
                    return parentDrawable.getLeft() + TAB;
                } else {
                    return 50;
                }
            }

            public abstract int getWidth();

            public int getRight() {
                return getLeft() + getWidth();
            }

            public int getTop() {
                if (aboveDrawable != null) {
                    return aboveDrawable.getBottom() + 3;
                } else if (parentDrawable != null) {
                    return parentDrawable.getTop() + 3;
                } else {
                    return 50;
                }
            }

            public abstract int getHeight();

            public int getBottom() {
                return getTop() + getHeight();
            }

            public final Rectangle getZone() {
                return new Rectangle(getLeft(), getTop(), getWidth(), getHeight());
            }

            public final Point getRoot() {
                return new Point(getLeft(), getTop());
            }

            public abstract void draw(Graphics2D g);
        }

        public static class DrawableTree extends DrawableStructure {

            List<DrawableStructure> content = new ArrayList<>();
            Color randColor = new Color(200 + rand.nextInt(50), 200 + rand.nextInt(50), 200 + rand.nextInt(50));

            public DrawableTree(String name, Color color) {
                super(name, color);
            }

            public void addContent(DrawableStructure struct) {
                struct.setParentDrawable(this);
                if (!content.isEmpty()) {
                    struct.setAboveDrawable(content.get(content.size() - 1));
                }
                content.add(struct);
            }

            @Override
            public int getWidth() {
                return content.stream().mapToInt(DrawableStructure::getWidth).max().orElse(0) + TAB + 3;
            }

            @Override
            public int getHeight() {
                return Math.max(content.stream().mapToInt(DrawableStructure::getHeight).sum(), textHeight) + 6 + (content.size() - 1) * 3;
            }

            @Override
            public void draw(Graphics2D g) {
                Rectangle zone = getZone();
                Point rootLeft = new Point(zone.x + 3, zone.y + 3);
                Point rootRight = new Point(zone.x + 800 + 3, zone.y + 3);

                //Draw rect
                new DrawableRectangle(zone.width, zone.height, randColor, Color.black).draw(rootLeft, g);
                new DrawableRectangle(zone.width, zone.height, randColor, Color.black).draw(rootRight, g);

                // Draw title
                Font oldFont = g.getFont();
                g.setFont(rotatedFont);
                int x = zone.x + TAB / 2 + hgt / 2;
                int y = zone.y + zone.height / 2 + getTextHeight() / 2;
                g.drawString(name, x, y);
                g.drawString(name, x + 800, y);
                //g.drawRect(x - hgt, y - getTextHeight(), hgt, getTextHeight());
                g.setFont(oldFont);

                // Now draw inner contents
                content.stream().forEach(drwbl -> drwbl.draw(g));
            }
        }

        public static class DrawableSideBySide extends DrawableStructure {

            DrawableCode left;
            DrawableCode right;

            public DrawableSideBySide(String name, Color color, DrawableCode left, DrawableCode right) {
                super(name, color);
                this.left = left;
                this.right = right;
            }

            @Override
            public int getWidth() {
                return Math.max(left.getWidth(), right.getWidth()); //800 - getLeft() + (int) right.getZone(getRoot()).getWidth();
            }

            @Override
            public int getHeight() {
                return Math.max(left == null ? 0 : left.getHeight(), right == null ? 0 : right.getHeight());
            }

            @Override
            public void draw(Graphics2D g) {
                Point root = getRoot();
                Point leftRoot = new Point(root.x + 3, root.y + 3);
                Point rightRoot = new Point(800 + root.x + 3, root.y + 3);
                left.draw(leftRoot, g);
                right.draw(rightRoot, g);
            }
        }

        public static interface DrawableCode {

            void draw(Point root, Graphics2D g);

            Rectangle getZone(Point root);

            int getHeight();

            int getWidth();
        }

        public static class DrawableRectangle implements DrawableCode {

            Color backgroundColor;
            Color borderColor;
            @Getter
            int w;
            @Getter
            int h;

            public DrawableRectangle(int w, int h, Color backgroundColor, Color borderColor) {
                this.h = h;
                this.w = w;
                this.backgroundColor = backgroundColor;
                this.borderColor = borderColor;
            }

            @Override
            public void draw(Point root, Graphics2D g) {
                Color old = g.getColor();
                Rectangle rect = getZone(root);
                g.setColor(backgroundColor);
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
                g.setColor(borderColor);
                g.drawRect(rect.x, rect.y, rect.width, rect.height);
                g.setColor(old);
            }

            @Override
            public Rectangle getZone(Point root) {
                return new Rectangle(root.x, root.y, w, h);
            }

            @Override
            public int getHeight() {
                return h;
            }

            @Override
            public int getWidth() {
                return w;
            }

        }

        @AllArgsConstructor
        public static class DrawableString implements DrawableCode {

            String[] lines;

            Color color;
            @Getter
            int w;
            @Getter
            int h;

            public DrawableString(String text, Color color) {
                super();
                this.lines = text.split("\n");
                int width = 0;
                for (String line : lines) {
                    width = Math.max(width, metrics.stringWidth(line));
                }
                this.w = width;
                this.h = lines.length * hgt;
                this.color = color;
            }

            @Override
            public int getHeight() {
                return h;
            }

            @Override
            public int getWidth() {
                return w;
            }

            @Override
            public void draw(Point root, Graphics2D g) {
                Color old = g.getColor();
                Rectangle rect = getZone(root);
                g.setColor(color);
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
                g.setColor(Color.BLACK);
                int lineNum = 0;
                for (String line : lines) {
                    g.drawString(line, rect.x, (rect.y + lineNum++ * hgt) + (hgt - 3));
                }
                g.setColor(old);
            }

            @Override
            public Rectangle getZone(Point root) {
                return new Rectangle(root.x, root.y, w, h);
            }
        }

        public static class DrawableCodeList implements DrawableCode {

            DrawableCode[] drawables;
            String name;
            int stringWidth;
            Color color;

            public DrawableCodeList(String name, Color color/* TODO unused*/, DrawableCode... inside) {
                super();
                this.name = name;
                this.color = color;
                this.drawables = inside;
                this.stringWidth = metrics.stringWidth(name);
            }

            @Override
            public void draw(Point root, Graphics2D g) {
                Rectangle zone = getZone(root);

                //Draw border
                g.setStroke(new BasicStroke(2));
                g.setColor(Color.red);
                g.drawRect(zone.x, zone.y, zone.width, zone.height);
                g.setStroke(new BasicStroke(1));
                g.setColor(Color.black);

                // Draw title
                Font oldFont = g.getFont();
                g.setFont(rotatedFont);
                g.drawString(name, zone.x + TAB / 2, zone.y + zone.height / 2 + stringWidth / 2);
                g.setFont(oldFont);

                // Draw content on top
                Point currentRoot = new Point(root.x + TAB, root.y);
                for (DrawableCode drawable : drawables) {
                    drawable.draw(currentRoot, g);
                    currentRoot = new Point(currentRoot.x, currentRoot.y + drawable.getHeight());
                }
            }

            @Override
            public Rectangle getZone(Point root) {
                Rectangle total = new Rectangle(root.x, root.y, 0, 0);
                Point currentRoot = root;
                Arrays.stream(drawables).forEach(d -> {
                    Rectangle zone = d.getZone(currentRoot);
                    total.setSize((int) Math.max(total.getWidth(), zone.getWidth() + TAB), (int) (total.getHeight() + zone.getHeight()));
                });
                return total;
            }

            @Override
            public int getHeight() {
                return Math.max(Arrays.stream(drawables).mapToInt(DrawableCode::getHeight).sum(), stringWidth);
            }

            @Override
            public int getWidth() {
                return Arrays.stream(drawables).mapToInt(DrawableCode::getHeight).max().orElse(TAB);
            }
        }



}
